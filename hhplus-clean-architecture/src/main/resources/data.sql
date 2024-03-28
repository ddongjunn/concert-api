insert into lecture (name, applicant_count, capacity_limit, start_date) values ('spring', '0', '30', now());
insert into lecture (name, applicant_count, capacity_limit, start_date) values ('k8s', '0', '30', TIMESTAMPADD(DAY, 1, NOW()));
insert into lecture (name, applicant_count, capacity_limit, start_date) values ('java', '0', '30', TIMESTAMPADD(DAY, -1, NOW()));
insert into lecture (name, applicant_count, capacity_limit, start_date) values ('tdd', '0', '30', TIMESTAMPADD(DAY, -2, NOW()));


