﻿import matplotlib.pyplot as plt
import numpy as np
import argparse
import math

figuresize = (
    (10.0, 2.6),
    (10.0, 4.0),
    (5.0, 3.0),
)

labelMap = {
    'WIND_DIR'  : 'Wind Direction (in degree)',
    'WIND_VEL1'  : 'Wind Velocity (in m/s)',
    'WIND_VEL2'  : 'Wind Velocity (in m/s)',
    'FOG'   : 'Fog (in TODO)',
    'RAIN'  : 'Rain (in mms)',
    'TEMP'  : 'Temperature (in DegC)',
    'TEMP_PT100'  : 'Temperature (in DegC)',
    'TEMP_PT1000'  : 'Temperature (in DegC)',
    'BAR_PRESS'  : 'Barometric Pressure (in hPa)',
    'HUM'   : 'Humidity (in %)',
    'MONTH' : 'Months',
    'HOUR'  : 'Hours',
    'DAY'   : 'Days',
    'YEAR'  : 'Years',
    'VOLT1'  : 'Voltage (in mV)',
    'VOLT2'  : 'Voltage (in mV)',
    'VOLT3'  : 'Voltage (in mV)',
    'VOLT4'  : 'Voltage (in mV)',
    'NOK'   : 'No Data'
}

colorMap = {
    'WIND_DIR' : '#76d1e3',
    'WIND_VEL1' : '#76d1e3',
    'WIND_VEL2' : '#76d1e3',
    'FOG'  : '#9da7b0',
    'RAIN' : '#042f66',
    'TEMP' : '#980598',
    'TEMP_PT100' : '#980598',
    'TEMP_PT1000' : '#980598',
    'BAR'  : '#62c20e',
    'HUM'  : '#77c7df',
    'VOLT1' : '#993366',
    'VOLT2' : '#993366',
    'VOLT3' : '#993366',
    'VOLT4' : '#993366',
    'NOK'  : '#e71109'
}

xTicsMap= {
    'MONTH' : ['UNKNOWN','Jan','Feb','Mae','Apr','Mai','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
}





def setDefaultStyle():
    plt.rcParams['figure.titleweight'] = 'bold'
    plt.rcParams['figure.figsize'] = figuresize[0]
    plt.rcParams['lines.linewidth'] = 1

    plt.rcParams['axes.titleweight'] = 'bold'
    plt.rcParams['axes.titlesize'] = 'medium'

    plt.rcParams['font.weight'] = 'bold'

    plt.rcParams['axes.labelweight'] = 'bold'
    plt.rcParams['xtick.labelsize'] = 'smaller'
    plt.rcParams['ytick.labelsize'] = 'smaller'

    # See available rcParams
    # plt.rcParams.keys

def format_basepower_e3(x, pos):
    return format_basepower_fixed_exponent(x, pos, 3)

def format_basepower_e4(x, pos):
    return format_basepower_fixed_exponent(x, pos, 4)

def format_basepower_e5(x, pos):
    return format_basepower_fixed_exponent(x, pos, 5)

def format_basepower_e6(x, pos):
    return format_basepower_fixed_exponent(x, pos, 6)

def format_basepower_e7(x, pos):
    return format_basepower_fixed_exponent(x, pos, 7)

def format_basepower_e8(x, pos):
    return format_basepower_fixed_exponent(x, pos, 8)

def normal_format(x, pos):
    return str(int(x/10)*10) 

def format_basepower_fixed_exponent(x, pos, exponent):
    base = 10
    if x == 0:
        return "0"
    elif x < 0:
        sign = True
        x = -x
    else:
        sign = False

    coefficient = int(x / base**exponent)

    if sign:
        return "$\mathbf{-" + str(coefficient) + "\cdot" + str(base) + "^{" + str(exponent) + "}}$"
    else:
        return "$\mathbf{" + str(coefficient) + "\cdot" + str(base) + "^{" + str(exponent) + "}}$"

def format_basepower(x, pos):
    base = 10
    precision = 1
    if x == 0:
        return "0"
    elif x < 0:
        sign = True
        x = -x
    else:
        sign = False

    exponent = int(math.log(x, base))
    coefficient = round(x / base**exponent, precision)

    #if coefficient >= base:
    #    coefficient = coefficient / base
    #    exponent = exponent + 1

    if sign:
        return "$-" + str(coefficient) + "\cdot" + str(base) + "^{" + str(exponent) + "}$"
    else:
        return "$" + str(coefficient) + "\cdot" + str(base) + "^{" + str(exponent) + "}$"


def  plot_graph(xs, ys, labels, plot_type , title, ylab, xlab,xtics,colormap):
  plots = []
  fig, ax = plt.subplots()
  ax.set_ylabel(labelMap[ylab],fontsize=16,labelpad=10)
  ax.set_xlabel(labelMap[xlab],fontsize=16,labelpad=10)
#  ax.grid(color='gray', alpha=0.5, linestyle='solid')
  ax.minorticks_on()

  # Customize the major grid
  ax.grid(which='major', linestyle='-', linewidth='0.5', color='black', alpha=0.5)
  # Customize the minor grid
  ax.grid(which='minor', linestyle=':', linewidth='0.5', color='gray')
  if xtics!= None:
    plt.xticks(np.arange(len(xtics)), xtics, rotation=45)
  
  plt.title(title)
  xtics_labels = []
  for i,(xl,yl,label,ls) in enumerate(zip(xs,ys,labels,['solid','none'])):
    for x,y in zip(xl,yl):
      xtics_labels += x
      if plot_type == 'bars':
        p = plt.bar(x, y, align='center', label=label, color=colormap[i], zorder=i)
      else:
        p = plt.plot(x, y, marker='o', linestyle=ls,linewidth=2, markersize=2.5, color=colormap[i], label=label)
      plots.append(p)

  xint = [x for x in range(min(xtics_labels), math.ceil(max(xtics_labels))+1)]
  if args.xTics == 'YEAR':
      plt.xticks(xint,xint, rotation=45)
  if args.xTics == 'YEAR':
      plt.xticks(xint, xint)


  plt.tight_layout()
  plt.show()

if __name__ == "__main__":
 parser = argparse.ArgumentParser()
 parser.add_argument('--file',      help='file name', type=str, required=True)
 parser.add_argument('--title',     help='plot title', type=str, required=True)
 parser.add_argument('--xTics',     help='Kind of x-axis (e.g MONTHS)', type=str, required=True)
# parser.add_argument("--labelList",help="list of labels", required=True, nargs='+')
 parser.add_argument("--label",     help="graph label", required=True, type=str)

 args = parser.parse_args()
 with open(args.file) as f:
      lines = [line.rstrip() for line in f]

 #if args.xTics == 'YEAR':
     #for line in lines:
         #spl=line.split()
         #if spl[1] == '-':
             #line = '#'
         #else:
             #break

     #for i in range(len(lines)-1, -1, -1):
        #spl=lines[i].split()
        #if spl[1] == '-':
            #lines[i] = '#'
        #else:
            #break
 x_ok = []
 x_nok = []
 y_ok = []
 y_nok = []
 last = -1
 ok = 0
 nok = 1
 for line in lines:
    spl=line.split()
    if(spl[0] == '#'):
        continue
    if(spl[1] == '-'):
      if last != nok:
          x_nok.append([])
          y_nok.append([])
          last = nok
      y_nok[-1].append(0.0)
      x_nok[-1].append(int(spl[0]))
    else:
      if last != ok:
            x_ok.append([])
            y_ok.append([])
            last = ok
      y_ok[-1].append(float(spl[1]))
      x_ok[-1].append(int(spl[0]))

 xtics = None 
 if args.xTics in xTicsMap:
  xtics = xTicsMap[args.xTics]
 plot_graph([x_ok, x_nok],[y_ok, y_nok], [args.label, 'No Data'], "", args.title, args.label, args.xTics, xtics, [colorMap[args.label],colorMap['NOK']]) 






