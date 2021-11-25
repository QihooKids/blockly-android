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