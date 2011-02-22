'''
Created on Feb 8, 2011

@author: uemit.seren
'''

import os
import simplejson
import re

class ChromosomeData(object):
    
    
    def __init__(self,data_folder,trackData):
        self.__lazyArrayChunks = {}
        self.__data_folder = data_folder
        self.__featureNCList = trackData['featureNCList']
        self.__sublistIndex = trackData['sublistIndex']
        self.__lazyIndex = trackData['lazyIndex']
        self.__subFeatureArray = trackData['subfeatureArray']
        self.__lazyFeatureFile = self.__data_folder + '/' + re.sub('{chunk}','%s',trackData['lazyfeatureUrlTemplate'])
        self.__subFeatureFile = self.__data_folder+'/'+self.__subFeatureArray['urlTemplate'].replace('{chunk}','%s')

    def getGenes(self,start,end,getFeatures=True):
        genes = []
        genes = self._getGenesFromNCList(self.__featureNCList,start, end,getFeatures,genes)
        return genes
    
    def _getGenesFromNCList(self,nclist,start,end,getFeatures = True,genes = []):
        length = len(nclist)
        i = self._binary_search(nclist,start)
        while ((i < length) and (i >= 0) and (nclist[i][0] < end)):
            if (isinstance(nclist[i][self.__lazyIndex],dict)):
                fp = open(self.__lazyFeatureFile % nclist[i][self.__lazyIndex]['chunk'])
                lazyFeatures = simplejson.load(fp)
                fp.close()
                genes = self._getGenesFromNCList(lazyFeatures,start,end,getFeatures,genes)
            else:
                gene = self._getGeneFeaturesForGene(nclist[i],getFeatures)
                genes.append(gene)
            if len(nclist[i]) >= self.__sublistIndex +1 and nclist[i][self.__sublistIndex] != None:
                genes = self._getGenesFromNCList(nclist[i][self.__sublistIndex],start,end,getFeatures,genes)
            i = i+1
        return genes
    
    def _getGeneFeaturesForGene(self,gene,getFeatures=True):
        stripped_gene = gene[0:4]
        stripped_gene.append([])
        if gene[4] != None and getFeatures:
            for subFeature in gene[4]:
                stripped_gene[4].append(self._getGeneFeaturesFromPos(subFeature,subFeature))
        return stripped_gene
    
    def _getGeneFeaturesFromPos(self,start,end):
        import math
        features = []
        start = max([0,start])
        end = min([end,self.__subFeatureArray['length']])
        firstChunk = int(math.floor(start / self.__subFeatureArray['chunkSize']))
        lastChunk = int(math.floor(end / self.__subFeatureArray['chunkSize']))

        for chunk in range(firstChunk,lastChunk+1):
            if not chunk in self.__lazyArrayChunks:
                fp = open(self.__subFeatureFile % chunk)
                lazyFeatures = simplejson.load(fp)
                fp.close()
                self.__lazyArrayChunks[chunk] = lazyFeatures
            features+=self._getGeneFeaturesFromChunk(chunk,start,end)
        return features
    
    def _getGeneFeaturesFromChunk(self,chunk,start,end):
        features = []
        chunkSize = self.__subFeatureArray['chunkSize']
        firstIndex = chunk*chunkSize
        chunkStart = max([start - firstIndex,0])
        chunkEnd = min([end - firstIndex,chunkSize-1])
        for i in range(chunkStart,chunkEnd+1):
            features+=self.__lazyArrayChunks[chunk][i]
        return features
    
    @classmethod
    def _binary_search(cls,arr, item, low=-1, high=None,index =1):
        if high is None:
            high = len(arr)
        while (high - low > 1):
            mid = (low + high) >> 1 
            midval = arr[mid][index] 
            if  midval > item:
                high = mid
            elif midval < item : 
                low = mid
           
        return high
        
class DataSource(object):
    '''
    classdocs
    '''
    

    def __init__(self,jbrowse_tracks_folder,track_key):
        '''
        Constructor
        '''
        self.__jbrowse_tracks_folder = jbrowse_tracks_folder
        self.__track_key = track_key
        self.__chromosomeSources = {}
        self.__genenametree = GeneNameTree(jbrowse_tracks_folder)
        dirList=os.listdir(self.__jbrowse_tracks_folder+"/data/tracks/")
        for fname in dirList:
            self.__initChromocomeSources(fname)
    
    def getGenes(self,chromosome,start,end,getFeatures=True):
        if chromosome not in self.__chromosomeSources:
            raise Exception('Chromosome Data-Source %s not found' % chromosome)
        genes = self.__chromosomeSources[chromosome].getGenes(start,end,getFeatures)
        return genes
    
    
    def __initChromocomeSources(self,fname):
        fp = open(self.__getChromosomeTrackFolder(fname)+'trackData.json')
        trackData = simplejson.load(fp)
        fp.close()
        self.__chromosomeSources[fname] = ChromosomeData(self.__jbrowse_tracks_folder,trackData)
    
    def __getChromosomeTrackFolder(self,chromosome):
        return self.__jbrowse_tracks_folder + '/data/tracks/%s/%s/' % (chromosome,self.__track_key)
    
    
    def getGeneFromName(self,query):
        return self.__genenametree.getGeneFromName(query)
    
    def getGenesFromQuery(self,query):
        return self.__genenametree.getGenesFromQuery(query)

class GeneNameTree(object):
    
    def __init__(self,jbrowse_tracks_folder):
        self.__jbrowse_tracks_folder = jbrowse_tracks_folder
        self.__nodes = self.__loadRootNode()
    
    def __loadRootNode(self):
        fp = open(self.__getRootNodeFolder()+'root.json')
        nodes = simplejson.load(fp)
        fp.close()
        return nodes
         
    def getGeneFromName(self,query):
        query = query.lower()
        qStart = 0
        path = []
        node = self.__nodes
        while True:
            childIndex = self.__binary_search(node,query[qStart])
            if childIndex < 0: return 
            path.append(childIndex)
            if isinstance(node[childIndex][0],int):
                node[childIndex] = self.__loadNode(path)
            node = node[childIndex]
        
            if query[qStart:qStart+len(node[0])] != node[0][0:min([len(node[0]),len(query) - qStart])]:
                return 
            qStart = qStart + len(node[0])
            if (qStart >=len(query)):
                if isinstance(node[1],list):
                    for gene in node[1]:
                        if  gene[1].lower() == query.lower():
                            gene[5] = ''
                            return gene
                return
    
    def getGenesFromQuery(self,query):
        genes = []
        query = query.lower()
        qStart = 0
        path = []
        node = self.__nodes
        while True:
            childIndex = self.__binary_search(node,query[qStart])
            if childIndex < 0: return
            path.append(childIndex)
            if isinstance(node[childIndex][0],int):
                node[childIndex] = self.__loadNode(path)
            node = node[childIndex]
            qStart = qStart + len(node[0])
            if qStart >= len(query):
                genes = self.__getAllGenesFromNode(node,genes,path)
                return genes
    
    def __getAllGenesFromNode(self,node,genes,path):
        if isinstance(node[1],list):
            for gene in node[1]:
                gene[5] = ''
                genes.append(gene)
        elif isinstance(node[0],int):
            node = self.__loadNode(path)
        
        for i,sub_nodes in enumerate(node[2:]):
            path.append(i+2)
            genes = self.__getAllGenesFromNode(sub_nodes, genes,path) 
        return genes
        
    def __getRootNodeFolder(self):
        return self.__jbrowse_tracks_folder + '/data/names/'
    
    def __getPathToPrefix(self,path):
        node = self.__nodes
        result = ''
        for i,index in enumerate(path):
            if isinstance(node[index][0],int):
                result = result + str(node[index][1])
            else:
                result = result + str(node[index][0])
            node = node[index]
        return result
    
    def __loadNode(self,path):
        fp = open(self.__getRootNodeFolder() + 'lazy-%s.json' % str(self.__getPathToPrefix(path)))
        node = simplejson.load(fp)
        fp.close()
        return node
        
    def __binary_search(self,list,char):
        low = 2
        high = len(list) - 1
        while (low <= high):
            mid = (low + high) >> 1
            if isinstance(list[mid][0],int):
                midVal = list[mid][1][0]
            else:
                midVal = list[mid][0][0]
            if midVal < char:
                low = mid + 1
            elif midVal > char:
                high = mid - 1
            else:
                return mid
        
        return -(low +1)
            
            
    