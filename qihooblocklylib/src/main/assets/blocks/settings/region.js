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