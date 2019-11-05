#!/usr/bin/python

"""
File containing a script for the calculation of regression importance
"""

import sys

args = sys.argv
args.pop(0)
arg_sum = 0
for arg in args:
    arg_sum += float(arg)
arg_avg = arg_sum / len(args)

sqr_diff_sum = 0
for arg in args:
    sqr_diff_sum += (float(arg) - arg_avg)**2
variance = sqr_diff_sum / len(args)
print (variance)

