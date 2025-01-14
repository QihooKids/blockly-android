'use strict';

Blockly.Lua['time_available_select'] = function(block) {
  var value_select = Blockly.Lua.valueToCode(block, 'select', Blockly.Lua.ORDER_ATOMIC);
  var time_available = Blockly.Lua.provideFunction_("time_available", [
  "function " + Blockly.Lua.FUNCTION_NAME_PLACEHOLDER_ + "(start, en)",
  "  local hour = tonumber(os.date(\"%H\"))",
  "  local min = tonumber(os.date(\"%M\"))",
  "  local time = hour*60 + min",
  "  if en <= start then",
  "    condition = condition and (time <= start or time >= en)",
  "  else",
  "    condition = condition and (time >= start or time <= en)",
  "  end",
  "end"]
  ),
  code = time_available+value_select+'\n';
  return code;
};
Blockly.Lua['ailab_root_block'] = function(block) {
  var statements_ai_setttings = Blockly.Lua.statementToCode(block, 'ai_setttings');
  var statements_ai_functions = Blockly.Lua.statementToCode(block, 'ai_functions');
  var statements_ai_events = Blockly.Lua.statementToCode(block, 'ai_events');
  var and =
  '    if not value then\n'+
  '      return ret\n'+
  '    end'
  ;

  var or =
   '    if value then\n'+
   '      break\n'+
   '    end \n'
   ;

  var code =
  'detect_ret = {}'+'\n'+
  'ret = ""'+'\n' +
  'condition = true'+'\n' +
  'region = {}'+'\n' +
  'sensitive = 0.5'+'\n' +
  'skill_ret = {}'+'\n' +
  'function reset()'+'\n' +
  '  detect_ret = {}'+'\n'+
  '  ret = ""'+'\n' +
  '  condition = true'+'\n' +
  '  region = {}'+'\n' +
  '  sensitive = 0.5'+'\n' +
  '  skill_ret = {}'+'\n' +
  'end'+'\n' +
  'function rule(detect_json)'+'\n' +
  '  reset()'+'\n' +
  '  detect_ret = cjson.decode(detect_json)'+'\n' +
  statements_ai_setttings+'\n'+
  '  print("blockly condition...",condition)\n'+
  '  if not condition then\n'+
  '    print("blockly return empty")\n'+
  '    return ret'+'\n' +
  '  end'+'\n' +
  statements_ai_functions + '\n'+
  '  if table_length(skill_ret) == 0 then \n'+
  '    print("blockly empty skill run...")\n'+
  '    return ret\n'+
  '  end\n'+
  '  for key, value in ipairs(skill_ret) do \n'+
  '    print("blockly check result key:",key) \n'+
  and + '\n'+
  '  end\n'+
  statements_ai_events +'\n'+
  '  print("return ret"..ret)\n'+
  '  return ret'+'\n' +
  'end'+'\n'
  ;
  return code;
};
Blockly.Lua['report_warning'] = function(block) {
  var text_title = block.getFieldValue('title');
  var text_des = block.getFieldValue('des');
  var checkbox_check = block.getFieldValue('check') == 'TRUE';
  var code = '\n';
  if(!checkbox_check){
     return 'ret = \"\" \n';
  }
  return code;
};
Blockly.Lua['result_run_rule'] = function(block) {
  var text_rule_id = block.getFieldValue('rule_id');
  // TODO: Assemble Lua into code variable.
  var code = '...\n';
  return code;
};
Blockly.Lua['region_set'] = function(block) {
  var text_title = block.getFieldValue('title');
  var text_des = block.getFieldValue('des');
  var value_region_arrays = Blockly.Lua.valueToCode(block, 'region_arrays', Blockly.Lua.ORDER_ATOMIC);
  var code = 'region={'+value_region_arrays+'}\n';
  return code;
};
Blockly.Lua['sensitive_high'] = function(block) {
  var text_title = block.getFieldValue('title');
  var code = '0.2';
  return [code, Blockly.Lua.ORDER_HIGH];
};
Blockly.Lua['sensitive_low'] = function(block) {
  var text_title = block.getFieldValue('title');
  var code = '0.8';
  return [code, Blockly.Lua.ORDER_HIGH];
};
Blockly.Lua['sensitive_medium'] = function(block) {
  var text_title = block.getFieldValue('title');
  var code = '0.5';
  return [code, Blockly.Lua.ORDER_HIGH];
};
Blockly.Lua['sensitive_select'] = function(block) {
  var value_select = Blockly.Lua.valueToCode(block, 'select', Blockly.Lua.ORDER_ATOMIC)||'=0.5';
  var sensitive = Blockly.Lua.provideFunction_("sensitive_select", [
    "function " + Blockly.Lua.FUNCTION_NAME_PLACEHOLDER_ + "(s)",
    "  sensitive = s",
    "end"]
    ),
    code = sensitive+value_select +'\n';
  return code;
};
Blockly.Lua['sensitive_set'] = function(block) {
  var dropdown_sensitive_select = block.getFieldValue('sensitive_select');
  // TODO: Assemble Lua into code variable.
  var code = '...\n';
  return code;
};
Blockly.Lua['time_available1'] = function(block) {
  var code = '0,0';
  return [code, Blockly.Lua.ORDER_HIGH];
};
Blockly.Lua['time_available2'] = function(block) {
  var code = '8*60,20*60';
  return [code, Blockly.Lua.ORDER_HIGH];
};
Blockly.Lua['time_available3'] = function(block) {
  var code = '20*60,8*60';
  return [code, Blockly.Lua.ORDER_HIGH];
};
Blockly.Lua['time_duration_set'] = function(block) {
  var text_hour_s = block.getFieldValue('hour_s');
  var text_min_s = block.getFieldValue('min_s');
  var text_hour_e = block.getFieldValue('hour_e');
  var text_min_e = block.getFieldValue('min_e');
  var code = text_hour_s+'*60+'+text_min_s+", "+text_hour_e+'*60+'+text_min_e;
  return code;
};
Blockly.Lua['xy'] = function(block) {
  var number_x = block.getFieldValue('x');
  var number_y = block.getFieldValue('y');
  var value_xy = Blockly.Lua.valueToCode(block, 'xy', Blockly.Lua.ORDER_ATOMIC) || "";
  var code = '{'+number_x+","+number_y+"}";
  if(value_xy.length > 0){
      code = code+","+value_xy;
  }
  return [code, Blockly.Lua.ORDER_ATOMIC];
};
Blockly.Lua['region'] = function(block) {
  var text_title = block.getFieldValue('title');
  var value_xy_arrays = Blockly.Lua.valueToCode(block, 'xy_arrays', Blockly.Lua.ORDER_ATOMIC);
  var value_next_region = Blockly.Lua.valueToCode(block, 'next_region', Blockly.Lua.ORDER_ATOMIC) || "";
    var code = '{'+value_xy_arrays+"}";
    if(value_next_region.length > 0){
        code = code+","+value_next_region;
    }
  return [code, Blockly.Lua.ORDER_ATOMIC];
};
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
    "    local sen_ret = value[\"rect\"][\"conf\"]",
    "    if sen_ret > sensitive then",
    "      local conf = 1",
    "      if next(region) ~= nil then",
    "        conf = qai_check_in_area(x, y, w, h, region)",
    "      end",
    "      if conf > 0.5 then",
    "        ret = \"qai_human_motion\"",
    "        skill_ret[\"human_detect\"] = true",
    "      end",
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