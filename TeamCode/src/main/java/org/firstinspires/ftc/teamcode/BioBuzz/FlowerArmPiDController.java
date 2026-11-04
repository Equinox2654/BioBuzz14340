package org.firstinspires.ftc.teamcode.BioBuzz;

public class FlowerArmPiDController {
     private double kP, kI, kD;
    private double integral = 0, lastError = 0;
    private double integralLimit = 0.25;   // anti-windup clamp
    private long lastTime = System.nanoTime();

    public FlowerArmPiDController(double kP, double kI, double kD) {
        this.kP = kP; this.kI = kI; this.kD = kD;
    }

    public double calculate(double target, double current) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;
        if (dt <= 0) return 0;

        double error = target - current;
        integral += error * dt;
        integral = Math.max(-integralLimit, Math.min(integralLimit, integral));
        double derivative = (error - lastError) / dt;
        lastError = error;

        return kP * error + kI * integral + kD * derivative;
    }

    public void reset() { integral = 0; lastError = 0; lastTime = System.nanoTime(); }

    public void setPID(double kP, double kI, double kD) {
        this.kP = kP; this.kI = kI; this.kD = kD;
    }
}
