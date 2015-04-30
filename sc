#!/usr/bin/python
import provmodified as prov
import docker
import json
import subprocess, sys, os
from rdflib import plugin, BNode



def main():
    #no user input error handling here. I assume user always input right args.
    cmd = sys.argv
     
    if cmd[1] == "genMeta":
        docker.bind_ns("test", "http://tw.rpi.edu/ns/test#")
        obj = cmd[2]
        dic = docker.inspect_json("docker inspect "+obj)
        
        graph = initialGraph(obj,dic)
        print(docker.serialize(format="turtle"))
    else:
        print "errors"
        
        
def initialGraph(obj,dic):
        
        parent = BNode()
        
        id = docker.get_id(dic)
        
        type = docker.get_type(dic)
        
        if type == "Image":
                container = docker.get_container(dic)
                parentImage = docker.get_parentImage(dic)
                if parentImage != "":
                        dataset = docker.inspect_json("docker inspect "+parentImage)
                        parent = initialGraph(parentImage,dataset)
                        
        elif type == "Container":
                image = docker.get_image(dic)
                
        else:
                return
        #docker.bind_ns("test", "http://tw.rpi.edu/ns/test#")
        image = docker.Image("docker inspect "+obj,"test:"+id)
        image.set_label("example image")
        image.set_was_derived_from(parent)
        return image
        
        
        
        
        
        

if __name__ == "__main__":
    main()