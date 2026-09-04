package cn.jstxjf_.rte.debug;

import cn.jstxjf_.rte.common.math.RailCalculator;

public class MathVerification {

    public static void main(String[] args) {
        System.out.println("=== Starting RTE Math Verification ===");
        boolean allPassed = true;

        System.out.println("[Test 1] RailCalculator Geometry");
        try {
            RailCalculator.Group group = RailCalculator.calculate(0, 0, 10, 0, 0, 0);
            if (group != null && group.first.isValid()) {
                System.out.println("  [PASS] Straight Line Calculation");
            } else {
                System.out.println("  [FAIL] Straight Line Calculation returned null or invalid group");
                allPassed = false;
            }

            double rad90 = Math.toRadians(90);
            RailCalculator.Group curveGroup = RailCalculator.calculate(0, 0, 10, 10, 0, rad90);
            if (curveGroup != null && curveGroup.first.isValid()) {
                System.out.println("  [PASS] 90-Deg Curve Calculation");
            } else {
                System.out.println("  [FAIL] 90-Deg Curve Calculation returned null");
                allPassed = false;
            }
        } catch (Exception e) {
            System.out.println("  [FAIL] Exception in RailCalculator: " + e.getMessage());
            e.printStackTrace();
            allPassed = false;
        }

        System.out.println("\n=== Verification Result: " + (allPassed ? "PASSED" : "FAILED") + " ===");
    }
}
