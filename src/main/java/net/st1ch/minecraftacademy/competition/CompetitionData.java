package net.st1ch.minecraftacademy.competition;

import java.util.HashSet;
import java.util.Set;

public class CompetitionData {

    private long startTime = 0;
    private long endTime = 0;
    private final Set<String> collectedPoints = new HashSet<>();

    /**
     * Вызывается в момент запуска робота
     */
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
        this.collectedPoints.clear();
    }

    /**
     * Вызывается при достижении финиша
     */
    public void finish() {
        if (this.startTime > 0 && this.endTime == 0) {
            this.endTime = System.currentTimeMillis();
        }
    }

    /**
     * Возвращает время прохождения трассы в миллисекундах
     */
    public long getDuration() {
        if (startTime == 0) return 0;
        if (endTime == 0) {
            return System.currentTimeMillis() - startTime;
        }
        return endTime - startTime;
    }

    /**
     * Сброс состояния данных робота (например, при рестарте)
     */
    public void reset() {
        this.startTime = 0;
        this.endTime = 0;
        this.collectedPoints.clear();
    }

    /**
     * Добавляет точку интереса, если она ещё не была собрана
     */
    public void collectPoint(String pointId) {
        this.collectedPoints.add(pointId);
    }

    /**
     * Количество уникальных собранных точек
     */
    public int getPointsCount() {
        return this.collectedPoints.size();
    }

    /**
     * Проверка, запущено ли прохождение (то есть был ли старт, но не завершено)
     */
    public boolean isRunning() {
        return this.startTime > 0 && this.endTime == 0;
    }

    /**
     * Проверка, завершено ли прохождение
     */
    public boolean isFinished() {
        return this.startTime > 0 && this.endTime > 0;
    }

    /**
     * Время начала
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Время завершения
     */
    public long getEndTime() {
        return endTime;
    }
}