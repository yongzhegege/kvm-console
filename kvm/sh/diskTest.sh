#!/bin/sh
device=$1
log_file=fileio.log
Usage() {
echo "basename $0 [Device Directory]"
exit 0
}
if [ -z "$*" ] || [ $# -ne 1 ]; then
Usage
fi
cd $device
rm -rf fileio.log
for blksize in 4096 16384; do
##prepare
sysbench fileio  --threads=16 --file-num=1 --file-total-size=200M  prepare
for mode in rndrw; do
# for mode in seqrewr; do
echo "----$device $blksize $mode----" >> $log_file
#run
sysbench fileio --file-num=1 --file-total-size=200M --threads=16 --file-test-mode=$mode --file-block-size=$blksize run >> $log_file 2>&1
done
#cleanup
sysbench fileio --file-total-size=200M cleanup
done
