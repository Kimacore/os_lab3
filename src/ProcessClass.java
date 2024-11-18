public class ProcessClass {
    private int id;
    private int cpuTime;
    private int memory;
    private int arrivalTime;
    private int priority;
    private boolean completed;
    private boolean added;
    private boolean blocked;


    public ProcessClass(int id, int cpuTime, int memory, int arrivalTime, int priority) {
        this.id = id;
        this.cpuTime = cpuTime;
        this.memory = memory;
        this.arrivalTime = arrivalTime;
        this.priority = priority;

    }

    public int getId() {
        return id;
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public int getMemory() {
        return memory;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public boolean isBlocked() {
        return blocked;
    }
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    @Override
    public String toString() {
        return "ID: " + id + ", CPU: " + cpuTime + ", Memory: " + memory +
                ", Arrival: " + arrivalTime + ", Priority: " + priority;
    }
}