exclude :test_arg_concat, "needs investigation"
exclude :test_assign_in_conditional, "needs investigation"
exclude :test_dstr_disallowed_variable, "needs investigation"
exclude :test_dynamic_constant_assignment, "needs investigation"
exclude :test_here_document, "needs investigation"
exclude :test_invalid_char, "needs investigation"
exclude :test_method_block_location, "needs investigation"
exclude :test_named_capture_conflict, "missing warning in parser (#2147)"
exclude :test_question, "needs investigation"
exclude :test_unused_variable, "missing warning in parser (#2147)"
exclude :test_void_expr_stmts_value, "1; next; 2 is figured via compile.c.  IR can do equivalent for 9k.  Not a huge issue for 1.7.x barring real issue"
