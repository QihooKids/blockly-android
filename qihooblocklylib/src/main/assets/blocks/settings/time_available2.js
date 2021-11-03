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