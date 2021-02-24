package io.github.foundationgames.sandwichable.compat;

import com.google.gson.JsonObject;
import io.github.foundationgames.sandwichable.compat.module.AbstractModuleMember;

import java.util.List;
import java.util.Set;

public class CompatModule {
    public String modId;
    public List<? extends AbstractModuleMember> members;

    public CompatModule(String modId, List<? extends AbstractModuleMember> members) {
        this.modId = modId;
        this.members = members;
    }

    public void initialize() {
        for(AbstractModuleMember member : members) {
            member.onInitialized();
        }
    }
}
