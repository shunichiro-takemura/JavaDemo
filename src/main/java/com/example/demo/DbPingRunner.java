package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbPingRunner implements CommandLineRunner {

  private final JdbcTemplate jdbcTemplate;

  public DbPingRunner(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(String... args) {
    var one = jdbcTemplate.queryForObject("select 1", Integer.class);
    System.out.println("[DB PING] select 1 => " + one);
  }
}
