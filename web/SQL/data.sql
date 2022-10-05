USE mysns;

INSERT INTO user VALUES("kim@abc.com", "111", "김시민", now());
INSERT INTO user VALUES("lee@abc.com", "222", "이순신", now());
INSERT INTO user VALUES("kwon@abc.com", "333", "권율", now());

INSERT INTO feed(id, content) VALUES("kim@abc.com", "Hello");
INSERT INTO feed(id, content) VALUES("kwon@abc.com", "Aloha");