import matplotlib.pyplot as plt
import numpy as np
from copy import copy
import sys,os
import argparse

import matplotlib.ticker as mtick
from matplotlib.lines import Line2D
from matplotlib.pyplot import hist
from matplotlib.scale import LogScale


figuresize = (
    (10.0, 2.6),
    (10.0, 4.0),
    (5.0, 3.0),
)


labelMap={
  'WIND':'Wind (in TODO)',
  'FOG':'Fog (in TODO)',
  'RAIN':'Rain (in mms)',
  'TEMP':'Temperature (in DegC)',
  'HUM':'Humidity (in %)',
  'MONTH':'Months',
  'HOUR':'Hours',
  'DAY':'Days',
  'YEAR':'Years',
  'NOK' : 'No Data'
}

colorMap={
  'WIND':'#76d1e3',
  'FOG':'#9da7b0',
  'RAIN':'#042f66',
  'TEMP':'#980598',
  'HUM':'#77c7df',
  'NOK' : '#e71109'
}

xTicsMap= {
  'MONTH' : ['Jan','Feb','Mae','Apr','Mai','Jun','Jul','Aug','Sep','Oct','Nov','Dec']
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
#    f = open(filename, "r")
#    x, y, y2 = np.loadtxt(f, unpack=True, usecols=(0, 1, 2))
#    f.close()


 # x_max = max(x)
 # x_min = min(x)
 # y_min = 0    
 # y_max = np.max(ys)
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
  
#ax.xlim(x_min,x_max)
#ax.ylim(y_min,y_max)
#    plt.title('Paper first submissions of the last 2 decades')
  plt.title(title)
  for i,(x,y,label,ls) in enumerate(zip(xs,ys,labels,['solid','none'])):
    if plot_type=='bars':
      p = plt.bar(x, y, align='center', label=label, color=colormap[i], zorder=i)
    else:
      p = plt.plot(x, y, marker='o', linestyle=ls,linewidth=2, markersize=2.5, color=colormap[i], label=label)
    plots.append(p)

#  plt.legend(loc='upper left',fontsize=13)
  plt.tight_layout()
  plt.show()

if __name__ == "__main__":
 parser = argparse.ArgumentParser()
 parser.add_argument('--file', help='file name', type=str, required=True)
 parser.add_argument('--title',     help='plot title', type=str, required=True)
 parser.add_argument('--xTics',     help='Kind of x-axis (e.g MONTHS)', type=str, required=True)
# parser.add_argument("--labelList",help="list of labels", required=True, nargs='+')
 parser.add_argument("--label",help="graph label", required=True, type=str)

 args = parser.parse_args()
 with open(args.file) as f:
      lines = [line.rstrip() for line in f]
  
 x_ok = []
 x_nok = []
 y_ok = []
 y_nok = []
 for line in lines:
    spl=line.split()
    if(spl[1] == '-'):
      y_nok.append(0.0)
      x_nok.append(float(spl[0]))
    else:
      y_ok.append(float(spl[1]))
      x_ok.append(float(spl[0]))

 xtics = None 
 if args.xTics in xTicsMap:
  xtics = xTicsMap[args.xTics]
 plot_graph([x_ok, x_nok],[y_ok, y_nok], [args.label, 'No Data'], "", args.title, args.label, args.xTics, xtics, [colorMap[args.label],colorMap['NOK']]) 





