package egd.aws.ec2starter;

public enum Ec2Statuses {
//  0 : pending
//  16 : running
//  32 : shutting-down
//  48 : terminated
//  64 : stopping
//  80 : stopped
    PENDING(0), RUNNING(16), SHUTTING_DOWN(32), TERMINATED(48), STOPPING(64), STOPPED(80);
    private int status;

    private Ec2Statuses(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
