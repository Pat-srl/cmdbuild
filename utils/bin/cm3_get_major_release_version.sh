
#!/bin/bash

exec utils/bin/cm3_get_release_version.sh | sed -r 's#^([0-9]+([.][0-9]+)?).*$#\1#'
