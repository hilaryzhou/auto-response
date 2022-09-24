package com.hilary.web.service;

import com.hilary.web.model.Propaganda;

import java.util.List;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 12:58
 * @description:
 **/
public interface SdkService {
   List<Propaganda> list();

   void edit(Propaganda propaganda);

   void delete(String id);
}
