SELECT * FROM names
[# mybatis:where]
-- AND COMMENT
  [# th:if="${firstName} != null"] first_name = [(${firstName})] [/]
  [# th:if="${lastName} != null"] AND last_name = [(${lastName})] [/]
[/]