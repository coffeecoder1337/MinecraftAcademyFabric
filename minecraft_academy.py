import socket
import threading
import json
import time
import queue


class MinecraftAcademy:
    """
    Класс для управления роботом
    """
    def __init__(self, server_ip, token):
        self.server_ip = server_ip
        self.server_port = 5005
        self.token = token
        self._allowed = False
        self._running = False

        self._sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self._sock.sendto(json.dumps({"type": "ping", "token": self.token}).encode("utf-8"), (self.server_ip, self.server_port))
        # self.sock.settimeout(2.0)

        self._send_queue = queue.Queue()
        self._recv_queue = queue.Queue()

        self._latest_sensors = None
        self._lock = threading.Lock()

    def start(self):
        """Метод для подключения к серверу"""

        self._running = True
        self._send_packet({
            "type": "init",
            "token": self.token
        })
        threading.Thread(target=self._receive_loop, daemon=True).start()
        threading.Thread(target=self._send_loop, daemon=True).start()
        threading.Thread(target=self._process_loop, daemon=True).start()

    def stop(self):
        """Метод отключения робота от сервера. Используйе, если хотите завершить управление роботом"""

        self._sock.sendto(json.dumps({"type": "disconnect", "token": self.token}).encode("utf-8"), (self.server_ip, self.server_port))

        self._running = False
        self._sock.close()

    def _send_loop(self):
        while self._running:
            try:
                data = self._send_queue.get(timeout=1)
                self._sock.sendto(data, (self.server_ip, self.server_port))
            except queue.Empty:
                continue
            except Exception as e:
                print(f"[Send Error] {e}")

    def _receive_loop(self):
        while self._running:
            try:
                data, _ = self._sock.recvfrom(65536)
                self._recv_queue.put(data)
            except socket.timeout:
                continue
            except Exception as e:
                print(f"[Receive Error] {e}")

    def _process_loop(self):
        while self._running:
            try:
                data = self._recv_queue.get(timeout=1)
                self._handle_packet(data)
            except queue.Empty:
                continue

    def _handle_packet(self, data: bytes):
        try:
            message = json.loads(data.decode('utf-8'))
            if message.get("type") == "status":
                if message.get("status") == "granted":
                    self._allowed = True
                    print(f"[INFO] Разрешение получено.")
                else:
                    self._allowed = False
                    self._latest_sensors = None
                    print(f"[INFO] Ожидаем разрешения от сервера...")
            elif message.get("type") == "sensors":
                with self._lock:
                    self._latest_sensors = message
        except Exception as e:
            print(f"[Handle Error] {e}")

    def _send_packet(self, data: dict):
        """Помещает данные для отправки на сервер в очередь"""
        try:
            self._send_queue.put_nowait(json.dumps(data).encode('utf-8'))
        except queue.Full:
            print("[WARN] Очередь отправки переполнена")

    def control(self, linear_speed: float, angular_speed: float):
        """
        Управляет роботом на сервере

        :param linear_speed: линейная скорость робота [0, 100]
        :param angular_speed: уловая скорость робота [0, 100]
        """
        if not self._running:
            raise ValueError("Для управления роботом необходимо инциализировать подключение методом start()")
        if not self._allowed:
            print("[WARN] Робот ещё не активирован")
            self._latest_sensors = None
            return
        self._send_packet({
            "type": "control",
            "token": self.token,
            "linear_speed": linear_speed,
            "angular_speed": angular_speed
        })

    def get_sensors(self) -> dict:
        """
        Получение значений с датчиков робота

        :return: dict

        Examples:
        {
            "distance_sensors": [0, 20.0, 0],
            "lidar_2d": [0, 0, 10, 12, 14, 20, 20, ..., 0],
            "lidar_3d": [
                [0, 0, 10, 12, 14, 20, 20, ..., 0],
                [0, 0, 10, 12, 14, 20, 20, ..., 0],
                ...
                [0, 0, 10, 12, 14, 20, 20, ..., 0],
            ],
            "color": 50
        }

        distance_sensors - значения с трех дальномеров, расположенных в передней части робота под углами [-30, 0, 30]
        lidar_2d - значения дальномеров, расположенных с отступом в 10 градусов по оси X
        lidar_3d - несколько lidar_2d, расположенных с отступом в 15 градусов [-30; 30] по оси Y
        color - датчик яркости блока под роботом [0; 100]
        """
        with self._lock:
            return self._latest_sensors.copy() if self._latest_sensors else None