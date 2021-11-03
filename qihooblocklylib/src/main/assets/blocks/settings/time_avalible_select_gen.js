Blockly.Lua['time_available_select'] = function(block) {
  var value_select = Blockly.Lua.valueToCode(block, 'select', Blockly.Lua.ORDER_ATOMIC);
  var time_available = Blockly.Lua.provideFunction_("time_available", [
  "function " + Blockly.Lua.FUNCTION_NAME_PLACEHOLDER_ + "(start, end)",
  "  local hour = tonumber(os.date(\"%H\"))",
  "  local min = tonumber(os.date(\"%M\"))",
  "  local time = hour*60 + min",
  "  if end <= start then",
  "    condition = condition and (time <= start or time >= end)",
  "  else",
  "    condition = condition and (time >= start or time <= end)",
  "  end",
  "end"]
  ),
  code = time_available+'('+value_select+")"+'\n';
  return code;
};