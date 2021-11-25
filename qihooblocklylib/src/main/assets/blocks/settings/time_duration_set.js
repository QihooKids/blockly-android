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