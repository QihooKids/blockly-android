Blockly.Lua['ailab_root_block'] = function(block) {
  var statements_ai_setttings = Blockly.Lua.statementToCode(block, 'ai_setttings');
  var statements_ai_functions = Blockly.Lua.statementToCode(block, 'ai_functions');
  var statements_ai_events = Blockly.Lua.statementToCode(block, 'ai_events');
  var and =
  '    if not value then\n'+
  '      return';

  var or =
   '    if value then\n'+
   '      break';

  var code =
  'detect_ret = {}'+'\n'+
  'ret = ""'+'\n' +
  'condition = true'+'\n' +
  'region = {{{0,0},{1,0},{1,1},{0,1}}}'+'\n' +
  'sensitive = 0.5'+'\n' +
  'skill_ret = {}'+'\n' +
  'function rule(detect_json)'+'\n' +
  '  detect_ret = cjson.decode(detect_json)'+'\n' +
  statements_ai_setttings+'\n'+
  '  if not condition then\n'+
  '    return'+'\n' +
  statements_ai_functions + '\n'+
  '  if table_length(skill_ret) == 0 then \n'+
  '    return\n'+
  '  for key, value in  skill_ret do \n'+
  and + '\n'+
  statements_ai_events +'\n'+
  '  return ret'+'\n' +
  'end'+'\n'
  ;
  return code;
};