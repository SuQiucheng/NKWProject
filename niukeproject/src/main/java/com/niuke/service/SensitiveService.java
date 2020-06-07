package com.niuke.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    public static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    private TrieNode rootNode;

    //默认敏感词替换符
    private static final String DEFAULT_REPLACEMENT = "***";

    //读取敏感词
    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine())!=null){
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
        }
        catch (Exception e){
            logger.error("读取错误"+e.getMessage());
        }
    }

    //前缀树节点
    private class TrieNode{
        private boolean end = false;

        private Map<Character,TrieNode> subNodes = new HashMap<>();
        //添加节点
        void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }
        //得到子节点
        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }
        //判断是不是结尾
        boolean isKeywordEnd(){
            return end;
        }
        //初始化结尾
        void setKeywordEnd(boolean end){
            this.end = end;
        }
        //子节点的大小
        public int getSubNodeCount(){
            return subNodes.size();
        }
    }
    //添加敏感词到树中，构成前缀树
    private void addWord(String lineTxt){
        TrieNode tempNode = rootNode;

        for (int i = 0;i<lineTxt.length();i++){
            Character c = lineTxt.charAt(i);
            if (isSymbol(c)){
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            if (node==null){
                node = new TrieNode();
                tempNode.addSubNode(c,node);
            }
            tempNode = node;
            if (i == lineTxt.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }
    //文本中词可能加入符号，避免被检测到，需要判断字符是不是特殊符号
    private boolean isSymbol(char c){
        int ic = (int) c;
        return (ic < 0x2e80 || ic>0x9fff);
    }
    //实际过滤
    public String filter(String text){
        if (StringUtils.isEmpty(text)){
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        while (position<text.length()){
            Character c = text.charAt(position);
            if (isSymbol(c)){
                //假如是特殊符号，判断是不是已经开始进入树判断了
                //比如a*b*c,假如敏感词是bc，则第一个*会假如result中
                //而第二个*不会加入result中
                if (tempNode == rootNode){
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            //字符c是否在子结点中
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
                result.append(text.charAt(begin));
                position = begin+1;
                begin = begin+1;
                tempNode = rootNode;

            }
            //刚好是关键词的最后一个节点
            else if (tempNode.isKeywordEnd()){
                result.append(replacement);
                position = position+1;
                begin = position;
                tempNode = rootNode;
            }
            else {
                ++position;
            }

        }
        result.append(text.substring(begin));
        return result.toString();
    }
}
