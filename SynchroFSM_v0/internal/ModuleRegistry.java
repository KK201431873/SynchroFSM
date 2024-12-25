package internal;

import java.util.ArrayList;
import java.util.List;

public class ModuleRegistry {
    private static final List<Module> modules = new ArrayList<>();

    public static synchronized void register(Module module) {
        modules.add(module);
    }

    public static synchronized List<Module> getModules() {
        return new ArrayList<>(modules);
    }
}
