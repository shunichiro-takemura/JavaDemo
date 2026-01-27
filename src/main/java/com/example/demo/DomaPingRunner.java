package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.dao.PingDao;

@Component
public class DomaPingRunner implements CommandLineRunner {

  private final PingDao pingDao;

  public DomaPingRunner(PingDao pingDao) {
    this.pingDao = pingDao;
  }

  @Override
  public void run(String... args) {
    int one = pingDao.selectOne();
    System.out.println("[DOMA PING] select 1 => " + one);
  }
}
