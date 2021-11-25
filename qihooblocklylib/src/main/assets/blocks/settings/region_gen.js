Blockly.Lua['region_set'] = function(block) {
  var text_title = block.getFieldValue('title');
  var text_des = block.getFieldValue('des');
  var value_xy_arrays = Blockly.Lua.valueToCode(block, 'xy_arrays', Blockly.Lua.ORDER_ATOMIC);
  var region = Blockly.Lua.provideFunction_("region", [
      "function " + Blockly.Lua.FUNCTION_NAME_PLACEHOLDER_ + "(xys)",
      "  region = xys",
      "end"]
      ),
      code = region+'({'+value_xy_arrays+'})'+'\n';
  return code;
};