from hobo import HoboCSVReader
import argparse


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--iFile", help="input hobo file",type=str, required=True )
    parser.add_argument("--oFile", help="output text file",type=str, required=True )
    args = parser.parse_args()


    with HoboCSVReader(args.iFile) as reader:

        print('Reading %s ...' % reader.fname)
        print('Serial Number: %s  Title: %s' % (reader.sn, reader.title))

        for timestamp, temperature, rh, battery in reader:
            print('%s   %03.1f F   %02.1f %%   %.1f V' % (timestamp, temperature, rh, battery))