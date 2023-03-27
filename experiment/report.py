import os
from parse import parse
import csv

def parse_txt(file_name: str):
    ret = []
    with open(file_name, 'r') as f:
        for l in f.readlines():
            ret.append(tuple(map(lambda i: int(i), 
                                 parse('{} - committed: {}, aborted: {}, avg latency: {} ms', 
                                       l.strip())[1:])))
    return ret

def parse_csv(file_name: str):
    """ Parse avg (throughput, latency, 25th, 50th, 75th) """
    ret = [0, 0, 0, 0, 0]
    cnt = 0
    with open(file_name, 'r') as f:
        reader = csv.reader(f.readlines())
        next(reader)
        for r in reader:
            cnt += 1
            for n, e in enumerate(list(map(lambda i: float(i),
                                           (r[1], r[2], r[5], r[6], r[7])))):
                ret[n] += e
    for i in range(len(ret)):
        ret[i] /= cnt
    return ret

def merge_lists(lists: list):
    """ Get average value in lists """
    return list(sum(l) / len(lists) for l in zip(*lists))

def compare(base: list, comp: list):
    ret = list(0 for _ in range(len(base)))
    for n, (b, c) in enumerate(zip(base, comp)):
        ret[n] = c / b
    return ret

def parse_csvs(file_list: list):
    ret = []
    for f in file_list:
        ret.append(parse_csv(f))
    return merge_lists(ret)

def jdbc_sp():
    with open('jdbc_sp.csv', 'w') as f:
        writer = csv.writer(f)
        writer.writerow('throughput, latency, 25th, 50th, 75th'.split(','))
        for plat in range(1, 4):
            env = f'env{plat}'
            print(env + ":")
            files = os.listdir(env)
            jdbc = parse_csvs(map(lambda s: f'{env}/{s}', filter(lambda s: 'jdbc' in s and 'csv' in s, files)))
            sp = parse_csvs(map(lambda s: f'{env}/{s}', filter(lambda s: 'sp' in s and 'csv' in s, files)))
            # res = compare(jdbc, sp)
            res = compare(sp, jdbc)
            print(f'jdbc over sp: {res}')
            writer.writerow(res)

def rw_ratio():
    ratio = {}
    for plat in range(1, 4):
        env = f'env{plat}'
        csvs = filter(lambda f: '.csv' in f, os.listdir(env))
        for case in csvs:
            _, tag, _ = parse('{}_{}_{}.csv', case)
            if tag not in ratio:
                ratio[tag] = []
            ratio[tag].append(f'{env}/{case}')
    for k, v in ratio.items():
        ratio[k] = parse_csvs(v)
    l = sorted([(k, v) for k, v in ratio.items()], key=lambda a: -int(a[0]))
    l = [(k, compare(l[0][1], v)) for k, v in l]
    print(l)
    with open('rw_ratio.csv', 'w') as f:
        writer = csv.writer(f)
        writer.writerow('ratio(r), throughput, latency, 25th, 50th, 75th'.split(','))
        for _l in l:
            writer.writerow((_l[0], *_l[1]))

def platform():
    with open('platform.csv', 'w') as f:
        writer = csv.writer(f)
        writer.writerow('platform, throughput, latency, 25th, 50th, 75th'.split(','))
        for plat in range(1, 4):
            env = f'env{plat}'
            csvs = filter(lambda f: '.csv' in f, os.listdir(env))
            ls = []
            for case in csvs:
                _, tag, _ = parse('{}_{}_{}.csv', case)
                ls.append(f'{env}/{case}')
            ls = parse_csvs(ls)
            writer.writerow((env, *ls))

# print(parse_txt('./env1/jdbc_0_100.txt'))
# print(parse_csv('./env1/jdbc_0_100.csv'))

jdbc_sp()
rw_ratio()
platform()