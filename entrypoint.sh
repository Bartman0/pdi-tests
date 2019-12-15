#!/bin/sh

run_pan() {
	echo ./pan.sh -file $@
	./pan.sh -file ../jobs/$@
}

run_kitchen() {
	echo ./kitchen.sh -file $@
	./kitchen.sh -file ../jobs/$@
}

print_usage() {
echo "

usage:	$0 COMMAND

Pentaho Data Integration (PDI)

commands:
  run_job filename	Run job file
  run_trans filename	Run transformation file
  help		        Print this help
"
}

rc=0

case "$1" in
    help)
        print_usage
        exit 1
        ;;
    run_trans)
        shift 1
        run_pan "$@"
        rc=$?
        ;;
    run_job)
        shift 1
        run_kitchen "$@"
        rc=$?
        ;;
    *)
        exec "$@"
        exit $?
        ;;
esac

exit $rc
