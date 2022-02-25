package github.microgalaxy.mqtt.broker.data.model;


import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 * <p>
 * node管理（名称/ip：port  cpu  memory  onlineStatus  运行时间  系统时间）
 */
public class NodePerformanceModel {
    private String brokerId;

    /**
     * windows10、linux
     */
    private String platform;

    private Integer cpuTotal;
    /**
     * kb
     */
    private Integer memoryTotal;

    private LRU<Long, Integer> threadNumber = new LRU<>(60);

    /**
     * timeStamp : rate
     */
    private LRU<Long, Double> cpu = new LRU<>(60);

    /**
     * timeStamp : rate
     */
    private LRU<Long, Double> memory = new LRU<>(60);
    /**
     * 节点离线（断开集群）
     */
    private boolean online;

    private Date startupTime;

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getCpuTotal() {
        return cpuTotal;
    }

    public void setCpuTotal(Integer cpuTotal) {
        this.cpuTotal = cpuTotal;
    }

    public Integer getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(Integer memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public LRU<Long, Integer> getThreadNumber() {
        return threadNumber;
    }

    public void putThreadNumber(Integer threadNumber) {
        this.threadNumber.put(System.currentTimeMillis(), threadNumber);
    }

    public LRU<Long, Double> getCpu() {
        return cpu;
    }

    public void putCpu(Double cpuRate) {
        cpu.put(System.currentTimeMillis(), cpuRate);
    }

    public LRU<Long, Double> getMemory() {
        return memory;
    }

    public void putMemory(Double memoryRate) {
        memory.put(System.currentTimeMillis(), memoryRate);
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Date getStartupTime() {
        return startupTime;
    }

    public void setStartupTime(Date startupTime) {
        this.startupTime = startupTime;
    }

    public Date getSystemTime() {
        return new Date();
    }

}
