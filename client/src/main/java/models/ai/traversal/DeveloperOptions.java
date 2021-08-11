package models.ai.traversal;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class DeveloperOptions {
    private TraversalEnum option;
    private AtomicInteger maxDeath;
    private int secWork;

    public DeveloperOptions(TraversalEnum option, int maxDeath, int secWork) {
        this.option = option;
        this.maxDeath = new AtomicInteger(maxDeath);
        this.secWork = secWork;
    }

    public int getMaxDeath() {
        return maxDeath.get();
    }

    public void setMaxDeath(int maxDeath) {
        this.maxDeath.set(maxDeath);
    }
}
