SELECT * FROM names
[# mybatis:where]
  [# th:if="${firstName} != null"] first_name = [(${firstName})] [/]
  [# th:if="${lastName} != null"] AND last_name = [(${lastName})] [/]
[/]