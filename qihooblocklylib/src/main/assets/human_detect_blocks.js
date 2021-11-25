Blockly.Blocks['report_warning'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("警报消息推送"), "title")
        .appendField(new Blockly.FieldTextInput("推送提示消息"), "des")
        .appendField(new Blockly.FieldCheckbox("TRUE"), "check");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['result_run_rule'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("执行规则下一个")
        .appendField(new Blockly.FieldTextInput("1002"), "rule_id");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['region_set'] = {
  init: function() {
    this.appendValueInput("xy_arrays")
        .setCheck("xy")
        .appendField(new Blockly.FieldTextInput("侦测区域"), "title")
        .appendField(new Blockly.FieldTextInput("可以设置重点关注的区域"), "des");
    this.appendDummyInput();
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['sensitive_high'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("高"), "title");
    this.setOutput(true, "sensitive");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['sensitive_low'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("低"), "title");
    this.setOutput(true, "sensitive");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['sensitive_medium'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("中"), "title");
    this.setOutput(true, "sensitive");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['sensitive_select'] = {
  init: function() {
    this.appendValueInput("select")
        .setCheck("sensitive")
        .appendField(new Blockly.FieldTextInput("灵敏度配置"), "title");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['sensitive_set'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("选择识别灵敏度")
        .appendField(new Blockly.FieldDropdown([["高","1"], ["中","2"], ["低","3"]]), "sensitive_select");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['time_available1'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("全天警戒"), "title")
        .appendField(new Blockly.FieldTextInput("24小时"), "des");
    this.setOutput(true, "time_available");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['time_available2'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("白天警戒"), "title")
        .appendField(new Blockly.FieldTextInput("8:00-20:00"), "des");
    this.setOutput(true, "time_available");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['time_available3'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("夜晚警戒"), "title")
        .appendField(new Blockly.FieldTextInput("20:00-次日8:00"), "des");
    this.setOutput(true, "time_available");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['time_available_select'] = {
  init: function() {
    this.appendValueInput("select")
        .setCheck("time_available")
        .appendField(new Blockly.FieldTextInput("智能看护时间段"), "title")
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['time_duration_set'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("自定义时间段"), "title")
        .appendField(new Blockly.FieldTextInput("10"), "hour_s")
        .appendField("时")
        .appendField(new Blockly.FieldTextInput("30"), "min_s")
        .appendField("分 至")
        .appendField(new Blockly.FieldTextInput("10"), "hour_e")
        .appendField("时")
        .appendField(new Blockly.FieldTextInput("30"), "min_e");
    this.setOutput(true, "time_available");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['xy'] = {
  init: function() {
    this.appendValueInput("xy")
        .setCheck("xy")
        .appendField("输入X坐标")
        .appendField(new Blockly.FieldNumber(0), "x")
        .appendField("Y坐标")
        .appendField(new Blockly.FieldNumber(0), "y");
    this.setOutput(true, "xy");
    this.setColour(230);
 this.setTooltip("单个坐标点xy的位置");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['human_detect'] = {
  init: function() {
    this.appendDummyInput()
        .appendField(new Blockly.FieldTextInput("人形检测"), "title")
        .appendField(new Blockly.FieldTextInput("针对有人出现的情况进行报警"), "des")
        .appendField(new Blockly.FieldCheckbox("TRUE"), "check");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};
Blockly.Blocks['ailab_root_block'] = {
  init: function() {
    this.appendStatementInput("ai_setttings")
        .setCheck(null)
        .appendField("添加AI识别配置");
    this.appendStatementInput("ai_functions")
        .setCheck(null)
        .appendField("添加需要执行的技能");
    this.appendStatementInput("ai_events")
        .setCheck(null)
        .appendField("如果技能执行成功那么执行");
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

