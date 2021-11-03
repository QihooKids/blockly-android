Blockly.Lua['human_detect'] = function(block) {
  var text_title = block.getFieldValue('title');
  var text_des = block.getFieldValue('des');
  var checkbox_check = block.getFieldValue('check') == 'TRUE';
  var human_detect = Blockly.Lua.provideFunction_("human_detect", [
    "function " + Blockly.Lua.FUNCTION_NAME_PLACEHOLDER_ + "()",
    "  base = qai_get_base_rect()",
    "  for index, value in ipairs(detect_ret[\"hand\"]) do",
    "    local x = value[\"rect\"][\"x\"]/base.w",
    "    local y = value[\"rect\"][\"y\"]/base.h",
    "    local w = value[\"rect\"][\"w\"]/base.w",
    "    local h = value[\"rect\"][\"h\"]/base.h",
    "    local conf = qai_check_in_area(x, y, w, h, region)",
    "    if conf > (1-sensitive) then",
    "      ret = \"qai_human_motion\"",
    "      skill_ret[\"human_detect\"] = true",
    "      return",
    "    end",
    "  end",
    "end"]
    );

  var code = "\n";
  if(checkbox_check){
    code = human_detect+'()\n';
  }
  return code;
};