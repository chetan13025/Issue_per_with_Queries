// validators/ValidatorRunner.java
package validators;

import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.ExtentTest;
import reports.ExtentCucumberListener;

/** Runs named validator blocks, collects all failures, then throws once. */
public final class ValidatorRunner {

    @FunctionalInterface
    public interface Block {
        void run() throws Exception; // your validators throw AssertionError/Exception
    }

    public static final class Validator {
        private final String name;
        private final Block block;
        public Validator(String name, Block block) {
            this.name = name;
            this.block = block;
        }
        public String name() { return name; }
        public Block block() { return block; }
    }

    /** Helper to make a validator with name + lambda */
    public static Validator of(String name, Block block) {
        return new Validator(name, block);
    }

    private static ExtentTest log() {
        return ExtentCucumberListener.getCurrentScenario();
    }

    /** Run all validators. Log pass/fail in Extent. Throw once if any failed. */
    public static void runAll(Validator... validators) {
        ExtentTest t = log();
        List<String> failures = new ArrayList<>();

        for (Validator v : validators) {
            try {
                if (t != null) t.info("🔎 Running validator: <b>" + v.name() + "</b>");
                v.block().run(); // your validator may throw AssertionError
                if (t != null) t.pass("✅ PASSED: " + v.name());
            } catch (AssertionError ae) {
                failures.add(v.name() + " → " + ae.getMessage());
                if (t != null) t.fail("❌ FAILED: " + v.name() + " → " + ae.getMessage());
            } catch (Exception e) {
                failures.add(v.name() + " → " + e.getMessage());
                if (t != null) t.warning("⚠️ ERROR in " + v.name() + " → " + e.getMessage());
            }
        }

        if (!failures.isEmpty()) {
            StringBuilder sb = new StringBuilder("One or more validators failed:\n");
            for (String f : failures) sb.append(" - ").append(f).append('\n');
            throw new AssertionError(sb.toString());
        }

        if (t != null) t.pass("🎉 All validators passed.");
    }

    private ValidatorRunner() {}
}
