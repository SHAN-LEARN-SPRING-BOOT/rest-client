package xyz.shanmugavel.poc.springboot.client.restclient.model;

public record User(long id, String name, String userName, String emails, Address address, String phone, String website, Company company) {

}
