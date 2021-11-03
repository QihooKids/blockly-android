Blockly.Lua['sensitive_select'] = function(block) {
  var value_select = Blockly.Lua.valueToCode(block, 'select', Blockly.Lua.ORDER_ATOMIC)||'=0.5';
  var sensitive = Blockly.Lua.provideFunction_("sensitive", [
    "function " + Blockly.Lua.FUNCTION_NAME_PLACEHOLDER_ + "(s)",
    "  sensitive = s",
    "end"]
    ),
    code = sensitive+value_select +'\n';
  return code;
};