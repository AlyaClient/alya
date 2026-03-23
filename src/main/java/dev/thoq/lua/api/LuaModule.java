package dev.thoq.lua.api;

import dev.thoq.Alya;
import dev.thoq.module.Category;
import dev.thoq.module.Module;
import dev.thoq.module.setting.BooleanSetting;
import dev.thoq.module.setting.ModeSetting;
import dev.thoq.module.setting.NumberSetting;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class LuaModule extends Module {
  private final LuaTable luaTable;
  private LuaFunction onEnableFunction;
  private LuaFunction onDisableFunction;

  public LuaModule(final String name, final String description, final Category category) {
    super(name, description, category);
    this.luaTable = buildTable();
  }

  public LuaModule(
      final String name, final String description, final Category category, final int keyCode) {
    super(name, description, category, keyCode);
    this.luaTable = buildTable();
  }

  private LuaTable buildTable() {
    final LuaTable table = new LuaTable();
    table.set(
        "getName",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(getName());
          }
        });
    table.set(
        "getDescription",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(getDescription());
          }
        });
    table.set(
        "getCategory",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(getCategory().name());
          }
        });
    table.set(
        "isEnabled",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(isEnabled());
          }
        });
    table.set(
        "toggle",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            toggle();
            return LuaValue.NIL;
          }
        });
    table.set(
        "enable",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            setEnabled(true);
            return LuaValue.NIL;
          }
        });
    table.set(
        "disable",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            setEnabled(false);
            return LuaValue.NIL;
          }
        });
    table.set(
        "getKeyCode",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(getKeyCode());
          }
        });
    table.set(
        "setKeyCode",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue keyCodeValue) {
            setKeyCode(keyCodeValue.toint());
            return LuaValue.NIL;
          }
        });
    table.set(
        "onEnable",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue callbackFunction) {
            if (callbackFunction instanceof LuaFunction)
              onEnableFunction = (LuaFunction) callbackFunction;
            return LuaValue.NIL;
          }
        });
    table.set(
        "onDisable",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue callbackFunction) {
            if (callbackFunction instanceof LuaFunction)
              onDisableFunction = (LuaFunction) callbackFunction;
            return LuaValue.NIL;
          }
        });
    table.set(
        "addBooleanSetting",
        new ThreeArgFunction() {
          @Override
          public LuaValue call(
              LuaValue nameValue, LuaValue descriptionValue, LuaValue defaultValue) {
            BooleanSetting booleanSetting =
                new BooleanSetting(
                    nameValue.tojstring(), descriptionValue.tojstring(), defaultValue.toboolean());
            initializeSettings(booleanSetting);
            return buildBooleanSettingTable(booleanSetting);
          }
        });
    table.set(
        "addNumberSetting",
        new VarArgFunction() {
          @Override
          public Varargs invoke(Varargs arguments) {
            String settingName = arguments.arg(1).tojstring();
            String settingDescription = arguments.arg(2).tojstring();
            double defaultValue = arguments.arg(3).todouble();
            double minValue = arguments.arg(4).todouble();
            double maxValue = arguments.arg(5).todouble();
            double incrementValue = arguments.narg() >= 6 ? arguments.arg(6).todouble() : 0.1;
            NumberSetting numberSetting =
                new NumberSetting(
                    settingName,
                    settingDescription,
                    defaultValue,
                    minValue,
                    maxValue,
                    incrementValue);
            initializeSettings(numberSetting);
            return buildNumberSettingTable(numberSetting);
          }
        });
    table.set(
        "addModeSetting",
        new VarArgFunction() {
          @Override
          public Varargs invoke(Varargs arguments) {
            String settingName = arguments.arg(1).tojstring();
            String settingDescription = arguments.arg(2).tojstring();
            String defaultValue = arguments.arg(3).tojstring();
            String[] modeOptions = new String[arguments.narg() - 3];
            for (int modeIndex = 0; modeIndex < modeOptions.length; modeIndex++) {
              modeOptions[modeIndex] = arguments.arg(modeIndex + 4).tojstring();
            }
            ModeSetting modeSetting =
                new ModeSetting(settingName, settingDescription, defaultValue, modeOptions);
            initializeSettings(modeSetting);
            return buildModeSettingTable(modeSetting);
          }
        });
    table.set(
        "getSetting",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue nameValue) {
            return getSetting(nameValue.tojstring())
                .map(
                    setting -> {
                      if (setting instanceof BooleanSetting)
                        return buildBooleanSettingTable((BooleanSetting) setting);
                      if (setting instanceof NumberSetting)
                        return buildNumberSettingTable((NumberSetting) setting);
                      if (setting instanceof ModeSetting)
                        return buildModeSettingTable((ModeSetting) setting);
                      return (LuaValue) LuaValue.NIL;
                    })
                .orElse(LuaValue.NIL);
          }
        });
    return table;
  }

  static LuaTable buildBooleanSettingTable(final BooleanSetting booleanSetting) {
    LuaTable settingTable = new LuaTable();
    settingTable.set(
        "getName",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(booleanSetting.getName());
          }
        });
    settingTable.set(
        "getValue",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(booleanSetting.getValue());
          }
        });
    settingTable.set(
        "isEnabled",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(booleanSetting.isEnabled());
          }
        });
    settingTable.set(
        "setValue",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue booleanValue) {
            booleanSetting.setValue(booleanValue.toboolean());
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "toggle",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            booleanSetting.toggle();
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "setVisibility",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue visibilityFunction) {
            if (visibilityFunction instanceof LuaFunction) {
              final LuaFunction luaFunction = (LuaFunction) visibilityFunction;
              booleanSetting.setVisibility(() -> luaFunction.call().toboolean());
            }
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "isVisible",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(booleanSetting.isVisible());
          }
        });
    return settingTable;
  }

  static LuaTable buildNumberSettingTable(final NumberSetting numberSetting) {
    LuaTable settingTable = new LuaTable();
    settingTable.set(
        "getName",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getName());
          }
        });
    settingTable.set(
        "getValue",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getValue());
          }
        });
    settingTable.set(
        "getValueAsInt",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getValueAsInt());
          }
        });
    settingTable.set(
        "getValueAsFloat",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf((double) numberSetting.getValueAsFloat());
          }
        });
    settingTable.set(
        "setValue",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue doubleValue) {
            numberSetting.setValue(doubleValue.todouble());
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "getMin",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getMin());
          }
        });
    settingTable.set(
        "getMax",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getMax());
          }
        });
    settingTable.set(
        "getIncrement",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getIncrement());
          }
        });
    settingTable.set(
        "isRangeEnabled",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.isRangeEnabled());
          }
        });
    settingTable.set(
        "setRangeEnabled",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue enabled) {
            numberSetting.setRangeEnabled(enabled.toboolean());
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "getSecondValue",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getSecondValue());
          }
        });
    settingTable.set(
        "getSecondValueAsInt",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getSecondValueAsInt());
          }
        });
    settingTable.set(
        "setSecondValue",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue doubleValue) {
            numberSetting.setSecondValue(doubleValue.todouble());
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "getRandomValue",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getRandomValue());
          }
        });
    settingTable.set(
        "getRandomValueAsInt",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.getRandomValueAsInt());
          }
        });
    settingTable.set(
        "setVisibility",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue visibilityFunction) {
            if (visibilityFunction instanceof LuaFunction) {
              final LuaFunction luaFunction = (LuaFunction) visibilityFunction;
              numberSetting.setVisibility(() -> luaFunction.call().toboolean());
            }
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "isVisible",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(numberSetting.isVisible());
          }
        });
    return settingTable;
  }

  static LuaTable buildModeSettingTable(final ModeSetting modeSetting) {
    LuaTable settingTable = new LuaTable();
    settingTable.set(
        "getName",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(modeSetting.getName());
          }
        });
    settingTable.set(
        "getValue",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(modeSetting.getValue());
          }
        });
    settingTable.set(
        "is",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue modeValue) {
            return LuaValue.valueOf(modeSetting.is(modeValue.tojstring()));
          }
        });
    settingTable.set(
        "setValue",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue modeValue) {
            modeSetting.setValue(modeValue.tojstring());
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "cycle",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            modeSetting.cycle();
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "getModes",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            LuaTable modesTable = new LuaTable();
            for (int modeIndex = 0; modeIndex < modeSetting.getModes().size(); modeIndex++) {
              modesTable.set(
                  modeIndex + 1, LuaValue.valueOf(modeSetting.getModes().get(modeIndex)));
            }
            return modesTable;
          }
        });
    settingTable.set(
        "setOnChange",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue changeFunction) {
            if (changeFunction instanceof LuaFunction) {
              final LuaFunction luaFunction = (LuaFunction) changeFunction;
              modeSetting.setOnChange(luaFunction::call);
            }
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "setVisibility",
        new OneArgFunction() {
          @Override
          public LuaValue call(LuaValue visibilityFunction) {
            if (visibilityFunction instanceof LuaFunction) {
              final LuaFunction luaFunction = (LuaFunction) visibilityFunction;
              modeSetting.setVisibility(() -> luaFunction.call().toboolean());
            }
            return LuaValue.NIL;
          }
        });
    settingTable.set(
        "isVisible",
        new ZeroArgFunction() {
          @Override
          public LuaValue call() {
            return LuaValue.valueOf(modeSetting.isVisible());
          }
        });
    return settingTable;
  }

  @Override
  public void onEnable() {
    if (onEnableFunction != null) {
      try {
        onEnableFunction.call();
      } catch (LuaError luaError) {
        Alya.getInstance()
            .getLogger()
            .error("Lua onEnable error in {}: {}", getName(), luaError.getMessage());
      }
    }
  }

  @Override
  public void onDisable() {
    if (onDisableFunction != null) {
      try {
        onDisableFunction.call();
      } catch (LuaError luaError) {
        Alya.getInstance()
            .getLogger()
            .error("Lua onDisable error in {}: {}", getName(), luaError.getMessage());
      }
    }
  }

  public LuaTable getLuaTable() {
    return luaTable;
  }
}
