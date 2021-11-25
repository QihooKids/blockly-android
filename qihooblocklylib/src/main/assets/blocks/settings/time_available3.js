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