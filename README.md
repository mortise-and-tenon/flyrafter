# FlyRafter

## 介绍

Java 实体类转为 SQL 建表文件，并可结合 `FlyWay` 使用的工具。

> `FlyRafter` 中文为 “飞椽”。在大式建筑中,为了增加屋檐挑出的深度,在原有圆形断面的檐椽的外端,还要加钉一截方形断面的椽子,这段方形断面的椽子就叫做“飞椽”,也叫“飞檐椽”。

## 特性

- 支持解析 JPA 相关注解标记的实体类，如 `@Entity`、`@Column`等
- 支持动态对比数据库表，生成相应 DDL
- 支持 `Spring Boot` 自动配置，开箱即用

## 快速开始

maven `pom.xml` 中，添加依赖项：

```xml
<dependency>
    <groupId>fun.mortnon</groupId>
    <artifactId>flyrafter</artifactId>
    <version>0.0.3</version>
</dependency>
```

## 更多

参阅 [Wiki](https://gitee.com/mortise-and-tenon/flyrafter/wikis/FlyRafter)
