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