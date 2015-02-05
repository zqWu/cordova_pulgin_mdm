# cordova_pulgin_mdm
a cordova mdm plugin


mdm command sample
```
{
	"info": "mdm",
	"abort_if_fail_one":"false"
	"list":[
	    {"type": "password_reset", "args": ["123456"]},
	    {"type": "password_min_len", "args": ["6"]},
	    {"type": "password_quality", "args": ["alpha_digit | alpha_digit_symbol"]},
	    {"type": "password_min_letter", "args": ["5"]},
	    {"type": "password_min_letter_low", "args": ["3"]},
	    {"type": "password_min_digit", "args": ["3"]},
	    {"type": "password_min_letter_low", "args": ["3"]},
	    {"type": "password_expire_time", "args": ["30000"]},
	    {"type": "password_history_restrict", "args": ["6"]},
	    {"type": "password_max_fail", "args": ["3"]},
	    {"type": "lock_screen", "args": []},
	    {"type": "timeout_screen", "args": ["3000"]},
	    {"type": "storage_crypt", "args": ["true|false"]}     
	]
}
```

test:
res/xml/config.xml,
index=index.html ->
index=mdm_test.html
