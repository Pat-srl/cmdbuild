
## CONFIG DIR

Config files in this dir are processed early at startup. Later they are mostly overwritten/replaced
with config values from db (with some exception).

The only exceptions are:

 * `database.conf` : db configs;
 * `cluster.conf` : cluster configs.

you may change these two files. You should not change any other files (changes to other files will be overwritten from db).