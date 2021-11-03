Blockly.Blocks['xy'] = {
  init: function() {
    this.appendValueInput("xy")
        .setCheck("xy")
        .appendField("输入X坐标")
        .appendField(new Blockly.FieldNumber(0, -Infinity, Infinity, 1), "x")
        .appendField("Y坐标")
        .appendField(new Blockly.FieldNumber(0, -Infinity, Infinity, 1), "y");
    this.setOutput(true, "xy");
    this.setColour(230);
 this.setTooltip("单个坐标点xy的位置");
 this.setHelpUrl("");
  }
};