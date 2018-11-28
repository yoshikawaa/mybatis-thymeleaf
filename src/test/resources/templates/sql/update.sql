UPDATE names
[# mybatis:set]
-- COMMENT ,
  [# th:if="${firstName} != null"] first_name = [(${firstName})], [/]
  [# th:if="${lastName} != null"] last_name = [(${lastName})] [/]
[/]
WHERE id = [(${id})]