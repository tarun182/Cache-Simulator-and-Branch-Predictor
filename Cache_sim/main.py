#from argparse import BooleanOptionalAction
from cmath import inf
from functools import lru_cache
from os import curdir
import sys
import math

input = input()
cache_parameters = input.split(" ")

#Input Validation 
if len(cache_parameters) != 9:
    sys.exit("Invalid Input, Please recompile the simulator")

sizeOfBlock = int(cache_parameters[1])
sizeOfL1    = int(cache_parameters[2])
assocOfL1   = int(cache_parameters[3])
sizeOfL2    = int(cache_parameters[4])
assocOfL2   = int(cache_parameters[5])
replacement_policy = int(cache_parameters[6])
inclusion_property = int(cache_parameters[7])
trace_file_name = cache_parameters[8]

#check for each segmentation faults

class cache_content:
    l1_no_of_reads =0
    l1_no_of_read_misses =0
    l1_no_of_writes=0
    l1_no_of_write_misses =0
    l1_writebacks=0  

    l2_no_of_reads =0
    l2_no_of_read_misses =0
    l2_no_of_writes=0
    l2_no_of_write_misses =0
    l2_writebacks=0  

    l1_mem_writebacks =0




#validation for L1_set


# l1_cache_calulation
if sizeOfL1!=0:
    l1_sets              = sizeOfL1 // (sizeOfBlock * assocOfL1)
else: 
    l1_sets              = 0
l1_block_offset_size = int(math.log(sizeOfBlock, 2)) 
l1_set_index_size    = int(math.log(l1_sets,2))
l1_tag_size          = 32- l1_set_index_size - l1_block_offset_size


dirty = 0 
#initialization
l1_cache_matrix = []
for i in range(l1_sets):
    listofSets =[]
    for i in range(assocOfL1):
        listofSets.append([dirty, None])
    l1_cache_matrix.append(listofSets)

cur_list_matrix = []
for i in range(l1_sets):
    cur_list_matrix.append([])



# l2_cache_calulation
if sizeOfL2!=0:
    l2_sets              = sizeOfL2 // (sizeOfBlock * assocOfL2)
    l2_block_offset_size = int(math.log(sizeOfBlock, 2))
    l2_set_index_size    = int(math.log(l2_sets,2))
    l2_tag_size          = 32- l2_set_index_size - l2_block_offset_size
else: 
    l2_sets              = 0


l2_cache_matrix = []
for i in range(l2_sets):
    listofSets =[]
    for i in range(assocOfL2):
        listofSets.append([dirty, None])
    l2_cache_matrix.append(listofSets)

l2_cur_list_matrix = []
for i in range(l2_sets):
    l2_cur_list_matrix.append([])


replacement_policy_list = []
cur_list_matrix_replacement_policy = {}
for i in range(l1_sets):
    cur_list_matrix_replacement_policy[i]={}


##################### Definitions #############
def hex2Bin(hex_value):   ##for changing hex to binary 
    bits =32
    bin_output = bin(int(hex_value, 16))[2:].zfill(bits)
    return bin_output

def bin2Dec(index_value):
    dec_output=0
    for i in index_value:
        dec_output = dec_output*2 + int(i)
    return dec_output

def bin2Hex(bin_value):
    hex_value = hex(int(bin_value, 2))[2:]
    return hex_value

def make_cur_list_matrix_replacement_policy(replacement_policy_list):
    for index in range(len(replacement_policy_list)):
        bin_value = replacement_policy_list[index]
        tag   = bin_value[:l1_tag_size]
        index_value = bin_value[l1_tag_size: (l1_tag_size + l1_set_index_size)]
        index_value = bin2Dec(index_value)
        if tag in cur_list_matrix_replacement_policy[index_value]:
            cur_list_matrix_replacement_policy[index_value][tag].append(index)
        else:
            cur_list_matrix_replacement_policy[index_value][tag] = []
            cur_list_matrix_replacement_policy[index_value][tag].append(index)

def LRU_FIFO(set_index, tag, cache_level):
    if cache_level == 1: 
        cur_tag = cur_list_matrix[set_index].pop(0)
        cur_list_matrix[set_index].append(tag)
        return cur_list_matrix[set_index], cur_tag
    if cache_level == 2:
        cur_tag = l2_cur_list_matrix[set_index].pop(0)
        l2_cur_list_matrix[set_index].append(tag)
        return l2_cur_list_matrix[set_index], cur_tag

def optimal(set_index, tag, prop):
    tag_list = l1_cache_matrix[set_index]
    cur_max = -1
    for i in tag_list:
        cur_tag = i[1]
        if cur_list_matrix_replacement_policy[set_index][cur_tag][0] > cur_max:
            cur_max = cur_list_matrix_replacement_policy[set_index][cur_tag][0]
            replace_tag = cur_tag
    for i in range(len(l1_cache_matrix[set_index])):
        if l1_cache_matrix[set_index][i][1] == replace_tag:
            if l1_cache_matrix[set_index][i][0] == 1:
                cache_content.l1_writebacks += 1
            l1_cache_matrix[set_index][i][1] = tag 
            if prop == 'r':
                l1_cache_matrix[set_index][i][0] = 0
            if prop == 'w':
                l1_cache_matrix[set_index][i][0] = 1
            cur_list_matrix_replacement_policy[set_index][tag].pop(0)
            if len(cur_list_matrix_replacement_policy[set_index][tag]) == 0:
                cur_list_matrix_replacement_policy[set_index][tag].append(inf) 
            break  
             
    return

def l1_inclusion(set_index, tag):
    for i in range(len(l1_cache_matrix[set_index])):
        if l1_cache_matrix[set_index][i][1] ==  tag:
            if l1_cache_matrix[set_index][i][0] == 1:
                cache_content.l1_mem_writebacks +=1
            l1_cache_matrix[set_index][i][1] = None
            l1_cache_matrix[set_index][i][0] = 0
            cur_list_matrix[set_index].remove(tag)
            

def l1tol2tagchange(set_index, tag):
    bits = l1_set_index_size
    setIndexbin = bin(set_index)[2:].zfill(bits)
    bin_value = tag + setIndexbin
    tag   = bin_value[:l2_tag_size]
    index_value = bin_value[l2_tag_size: (l2_tag_size + l2_set_index_size)]
    return tag, bin2Dec(index_value)

def l2tol1change(set_index, tag):
    bits = l2_set_index_size
    setIndexbin = bin(set_index)[2:].zfill(bits)
    bin_value = tag + setIndexbin
    tag   = bin_value[:l1_tag_size]
    index_value = bin_value[l1_tag_size: (l1_tag_size + l1_set_index_size)]
    return tag, bin2Dec(index_value)

def l2_cache_insert(set_index, tag, prop):
    insert_flag = 0
    for i in range(assocOfL2):
        if (l2_cache_matrix[set_index][i][1] == None) and (insert_flag == 0):
            l2_cache_matrix[set_index][i][1] = tag
            insert_flag = 1
            if prop == 'r':
                l2_cache_matrix[set_index][i][0] = 0
            if prop == 'w':
                l2_cache_matrix[set_index][i][0] = 1
            l2_cur_list_matrix[set_index].append(tag)
            return
    if insert_flag == 0: #All the assoc_sets are non-empty, we should call replacement policy of the cache
        if replacement_policy == 0 or replacement_policy == 1:
            cache_level = 2
            l2_cur_list_matrix[set_index], replace_tag= LRU_FIFO(set_index, tag, cache_level)
            for i in range(len(l2_cache_matrix[set_index])):
                if l2_cache_matrix[set_index][i][1] == replace_tag:
                    if inclusion_property == 1:
                            taginc, set_indexinc = l2tol1change(set_index,replace_tag)
                            l1_inclusion(set_indexinc, taginc)
                    if l2_cache_matrix[set_index][i][0] == 1:
                        cache_content.l2_writebacks += 1
                    l2_cache_matrix[set_index][i][1] = tag 
                    if prop == 'r':
                        l2_cache_matrix[set_index][i][0] = 0
                    if prop == 'w':
                        l2_cache_matrix[set_index][i][0] = 1
                    break

def l1_cache_insert(set_index, tag, prop):
    insert_flag = 0
    for i in range(assocOfL1):
        if (l1_cache_matrix[set_index][i][1] == None) and (insert_flag == 0):
            l1_cache_matrix[set_index][i][1] = tag
            insert_flag = 1
            if prop == 'r':
                l1_cache_matrix[set_index][i][0] = 0
            if prop == 'w':
                l1_cache_matrix[set_index][i][0] = 1
            cur_list_matrix[set_index].append(tag)
            if replacement_policy == 2:
                cur_list_matrix_replacement_policy[set_index][tag].pop(0)
                if len(cur_list_matrix_replacement_policy[set_index][tag]) == 0:
                    cur_list_matrix_replacement_policy[set_index][tag].append(inf)
            return
    if insert_flag == 0: #All the assoc_sets are non-empty, we should call replacement policy of the cache
        if replacement_policy == 0 or replacement_policy == 1:
            cache_level =1
            l2_write_back_update = 0
            cur_list_matrix[set_index], replace_tag= LRU_FIFO(set_index, tag, cache_level)
            for i in range(len(l1_cache_matrix[set_index])):
                if l1_cache_matrix[set_index][i][1] == replace_tag:
                    if l1_cache_matrix[set_index][i][0] == 1:
                        cache_content.l1_writebacks += 1
                        if sizeOfL2!=0: 
                            tag2, set_index2 = l1tol2tagchange(set_index,replace_tag)
                            found = False
                            for j in range(len(l2_cache_matrix[set_index2])):
                                if l2_cache_matrix[set_index2][j][1] == tag2:
                                    l2_cache_matrix[set_index2][j][0] = 1 
                                    l2_cur_list_matrix[set_index2].pop(l2_cur_list_matrix[set_index2].index(tag2))
                                    l2_cur_list_matrix[set_index2].append(tag2)
                                    found = True 
                            if found == False:
                                cache_content.l2_no_of_write_misses +=1
                                l2_cache_insert(set_index2, tag2, "w")                     
                            cache_content.l2_no_of_writes += 1
                    l1_cache_matrix[set_index][i][1] = tag 
                    if prop == 'r':
                        l1_cache_matrix[set_index][i][0] = 0
                    if prop == 'w':
                        l1_cache_matrix[set_index][i][0] = 1
                    break
        if replacement_policy == 2:
            optimal(set_index, tag, prop)

def l2_cache(bin_value, prop):
    tag   = bin_value[:l2_tag_size]
    index_value = bin_value[l2_tag_size: (l2_tag_size + l2_set_index_size)]
    block_offset = bin_value[(l2_tag_size + l2_set_index_size):]
    set_index= bin2Dec(index_value)
    cache_content.l2_no_of_reads +=1
    if prop == 'r':
        for i in range(assocOfL2):
            if l2_cache_matrix[set_index][i][1] == tag:
                if replacement_policy == 0:
                    l2_cur_list_matrix[set_index].pop(l2_cur_list_matrix[set_index].index(tag))
                    l2_cur_list_matrix[set_index].append(tag)                          
                return           
        cache_content.l2_no_of_read_misses +=1
        l2_cache_insert(set_index, tag, prop)

        

    if prop == 'w':
        for i in range(assocOfL2):
            if l2_cache_matrix[set_index][i][1] == tag:
                if replacement_policy == 0:
                    l2_cur_list_matrix[set_index].pop(l2_cur_list_matrix[set_index].index(tag))
                    l2_cur_list_matrix[set_index].append(tag)
                return           
        cache_content.l2_no_of_read_misses +=1
        l2_cache_insert(set_index, tag, "r")



def l1_cache(prop, bin_value):      # Building L1 cache with all the properties
    tag   = bin_value[:l1_tag_size]
    index_value = bin_value[l1_tag_size: (l1_tag_size + l1_set_index_size)]
    block_offset = bin_value[(l1_tag_size + l1_set_index_size):]

    if prop == 'r':
        cache_content.l1_no_of_reads +=1
        set_index= bin2Dec(index_value)
        for i in range(assocOfL1):
            if l1_cache_matrix[set_index][i][1] == tag:
                if replacement_policy == 0:
                    cur_list_matrix[set_index].pop(cur_list_matrix[set_index].index(tag))
                    cur_list_matrix[set_index].append(tag)
                if replacement_policy == 2:
                    cur_list_matrix_replacement_policy[set_index][tag].pop(0)
                    if len(cur_list_matrix_replacement_policy[set_index][tag]) == 0:
                        cur_list_matrix_replacement_policy[set_index][tag].append(inf) 
                return           
        cache_content.l1_no_of_read_misses +=1
        ### read miss check in L2 Cache ###
        #code here      

        l1_cache_insert(set_index, tag, prop)
        if sizeOfL2!=0:
            l2_cache(bin_value, prop)
                
    
    if prop == 'w':
        cache_content.l1_no_of_writes +=1
        set_index= bin2Dec(index_value)
        for i in range(assocOfL1):
            if l1_cache_matrix[set_index][i][1] == tag:
                l1_cache_matrix[set_index][i][0] = 1
                if l1_cache_matrix[set_index][i][1] == tag:
                    if replacement_policy == 0:
                        cur_list_matrix[set_index].pop(cur_list_matrix[set_index].index(tag))
                        cur_list_matrix[set_index].append(tag)
                    if replacement_policy == 2:
                        cur_list_matrix_replacement_policy[set_index][tag].pop(0)
                        if len(cur_list_matrix_replacement_policy[set_index][tag]) == 0:
                            cur_list_matrix_replacement_policy[set_index][tag].append(inf) 
                return             
        cache_content.l1_no_of_write_misses +=1
        ### write miss check in L2 Cache ###
        #code here
        
        l1_cache_insert(set_index, tag, prop)
        if sizeOfL2!=0:
            l2_cache(bin_value, prop)


#Reading inputs from tracefile
path= "traces/" + trace_file_name
f = open(path,'r')

if replacement_policy == 2:
    with open(path) as f:
        for line in f.readlines():
            data = line.split(" ")
            property_of_value = data[0]
            hex_value= data[1]
            bin_value= hex2Bin(hex_value)
            replacement_policy_list.append(bin_value)
    make_cur_list_matrix_replacement_policy(replacement_policy_list)


with open(path) as f:
    for line in f.readlines():
        data = line.split(" ")
        property_of_value = data[0]
        hex_value= data[1]
        bin_value= hex2Bin(hex_value)
        l1_cache_update = l1_cache(property_of_value, bin_value)

replacement_policy_name = ""
if replacement_policy == 0:
    replacement_policy_name = "LRU"
if replacement_policy == 1:
    replacement_policy_name = "FIFO"
if replacement_policy == 2:
    replacement_policy_name = "optimal"

inclusive_property_name = ""
if inclusion_property == 0:
    inclusive_property_name = "non-inclusive"
if inclusion_property == 1:
    inclusive_property_name = "inclusive"


print("===== Simulator configuration =====")
print("BLOCKSIZE:            ",sizeOfBlock)
print("L1_SIZE:              ",sizeOfL1)
print("L1_ASSOC:             ",assocOfL1)
print("L2_SIZE:              ",sizeOfL2)
print("L2_ASSOC:             ",assocOfL2)
print("REPLACEMENT POLICY:   ",replacement_policy_name)
print("INCLUSION PROPERTY:   ",inclusive_property_name)
print("trace_file:           ",trace_file_name)

print("===== L1 contents =====")
l1_miss_rate = (cache_content.l1_no_of_read_misses + cache_content.l1_no_of_write_misses)/(cache_content.l1_no_of_reads+cache_content.l1_no_of_writes)
l1_miss_rate = round(l1_miss_rate,6)
l2_miss_rate = 0
mem_traffic = cache_content.l1_no_of_read_misses + cache_content.l1_no_of_write_misses + cache_content.l1_writebacks
if replacement_policy == 0 or replacement_policy == 1:
    for i in range(l1_sets):
        s = "Set     "+str(i)+":\t"
        for j in range(len(l1_cache_matrix[0])):
            if l1_cache_matrix[i][j][1]!= None:
                value = bin2Hex(l1_cache_matrix[i][j][1])
                s =  s+str(value)+ " "+ str("D" if l1_cache_matrix[i][j][0] == 1 else " ")
                s =  s+"  " 
        print(s)

    if (sizeOfL2 != 0):
        l2_miss_rate = cache_content.l2_no_of_read_misses/cache_content.l2_no_of_reads
        l2_miss_rate = round(l2_miss_rate,6)
        if inclusive_property_name == 0:
            mem_traffic = cache_content.l2_no_of_read_misses + cache_content.l2_no_of_write_misses + cache_content.l2_writebacks
        else:
            mem_traffic = cache_content.l2_no_of_read_misses + cache_content.l2_no_of_write_misses + cache_content.l2_writebacks + cache_content.l1_mem_writebacks
        print("===== L2 contents =====")
        for i in range(l2_sets):
            s = "Set     "+str(i)+":\t"
            for j in range(len(l2_cache_matrix[0])):
                if l2_cache_matrix[i][j][1]!= None:
                    value = bin2Hex(l2_cache_matrix[i][j][1])
                    s =  s+str(value)+ " "+ str("D" if l2_cache_matrix[i][j][0] == 1 else " ")
                    s =  s+"  " 
            print(s)
    
if replacement_policy == 2: 
        for i in range(l1_sets):
            s = "Set     "+str(i)+":\t"
            for j in range(len(l1_cache_matrix[0])):
                if l1_cache_matrix[i][j][1]!= None:
                    value = bin2Hex(l1_cache_matrix[i][j][1])
                    s =  s+str(value)+ " "+ str("D" if l1_cache_matrix[i][j][0] == 1 else " ")
                    s =  s+"  " 
            print(s)

print("===== Simulation results (raw) =====")
print("a. number of L1 reads:       ", cache_content.l1_no_of_reads)
print("b. number of L1 read misses: ", cache_content.l1_no_of_read_misses)
print("c. number of L1 writes:      ", cache_content.l1_no_of_writes)
print("d. number of L1 write misses:", cache_content.l1_no_of_write_misses)
print("e. L1 miss rate:             ",l1_miss_rate)
print("f. number of L1 writebacks:  ", cache_content.l1_writebacks)

print("g. number of L2 reads:       ", cache_content.l2_no_of_reads)
print("h. number of L2 read misses: ", cache_content.l2_no_of_read_misses)
print("i. number of L2 writes:      ", cache_content.l2_no_of_writes)
print("j. number of L2 write misses:", cache_content.l2_no_of_write_misses)
print("k. L2 miss rate:             ",l2_miss_rate)
print("l. number of L2 writebacks:  ", cache_content.l2_writebacks)
print("m. total memory traffic:     ",mem_traffic)
