import moderator

if __name__ == '__main__':
  name = "billy-bob"
  earlier_date = "2002-01-01T10:10:20"
  later_date = "2001-01-01T10:10:20"
  print("is_valid_name has", end=" ")
  if moderator.is_valid_name(name) == True:
    print("passed.")
  else:
    print("failed.")
    
  print("is_chronological has", end=" ")
  if moderator.is_chronological(earlier_date, later_date) == False:
    print("passed.")
  else:
    print("failed.")
