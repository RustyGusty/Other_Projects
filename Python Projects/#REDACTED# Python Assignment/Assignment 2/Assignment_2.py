def get_classes(filename : str) -> list:
  ''' Return a list of tuples with the class id and the class name in the order of filename '''
  f = open(filename)
  class_list = []

  for line in f:
    # line = f.readline()
    space_index = line.find(" ")
    class_id = int(line[:space_index])
    class_name = line[space_index + 1:].strip()
    class_list.append((class_id, class_name))

  return class_list

def get_trainers(filename : str) -> list:
  ''' Return a list of tuples with the trainer name and their hourly wage in the order of occurrence in filename '''
  # trainer_name --> hourly wage
  f = open(filename)
  trainer_list = []

  for line in f:
    # line = f.readline()
    space_index = line.find(" ")
    trainer_wages = int(line[:space_index])
    trainer_name = line[space_index + 1:].strip()
    trainer_list.append((trainer_wages, trainer_name))

  return trainer_list

def get_schedule(filename : str) -> list:
  ''' Returns a list of lists, where each row index corresponds to the trainer and each column represents whether or not a course is being taught by the trainer in the row (as read from filename) '''

  f = open(filename)
  schedule_list = []
  for line in f:
    trainer_schedule = []
    for ch in line.strip():
      trainer_schedule.append(int(ch))
    schedule_list.append(trainer_schedule)
  return schedule_list


def get_name_from_id(class_list : list) -> str:

  # list = [class_1, class_2, class_3, class_4]
  # ID 2: list[1] list[id - 1]
  class_id = input("Enter a class ID ---> ")
  if not class_id.isdigit():
    return "Class ID does not exist"
  class_id = int(class_id)

  # Find where class_id == class_list[0] (the ID) and return the name if so
  for class_tuple in class_list:
    if class_tuple[0] == class_id:
      return class_tuple[1]

  return "Class ID does not exist"

def get_id_from_name(class_list: list) -> str:
  class_name = input("Enter a class name ---> ")
  for class_tuple in class_list:
    if class_tuple[1] == class_name:
      return class_tuple[0]

    # class_tuple = (1, "PILATES")
    # class_tuple = (class_id, class_name)

  return "Class name does not exist"

def get_all_trainers_from_id(trainer_list: list, schedule_list):
  class_id = input("Enter Class ID -->")
  if not class_id.isdigit():
    return "No one is teaching this class"

  class_id = int(class_id)
  res = ""
  for i in range(len(trainer_list)):
    if schedule_list[i][class_id - 1]: # == 1 technically
      res += f"trainer_list[i][1],"
  if not trainer_list:
    return "No one is teaching this class"
  else:
    return res[:len(res) - 1]


  '''enter your choice ---> 3
Enter Class ID ---> 5
Amelia Grosh,Daniel Adams,Olive Greene'''
  '''enter your choice ---> 3
Enter Class ID ---> 4
No one is teaching this class'''


def get_wages(trainer_list : list, schedule_list : list) -> str:
  trainer_name = input("Enter Trainer Name ---> ")

  for i in range(len(trainer_list)):
    if trainer_name == trainer_list[i][1]:
      hours = sum(schedule_list[i])
      if hours == 0:
        return "No class is assigned to this trainer"
      hourly_wage = trainer_list[i][0]
      return hourly_wage * hours
  return "No trainer with this name exists"


def get_full_classes(class_list : list) -> str:
  res = ""
  for class_tuple in class_list:
    res += f"{class_tuple[0]} {class_tuple[1]}"
  return res

def get_all_trainers_from_id(trainer_list: list, schedule_list):
  class_id = input("Enter Class ID -->")
  if not class_id.isdigit():
    return "No one is teaching this class"

  class_id = int(class_id)
  res = ""
  if class_id > len(schedule_list[0]):
      return "Class ID does not exist"
  for i in range(len(trainer_list)):
    if schedule_list[i][class_id - 1]: # == 1 technically
      res += f"{trainer_list[i][1]},"
  if not trainer_list:
    return "No one is teaching this class"
  else:
    return res[:-1]


def get_full_schedule(class_list : list, trainer_list : list, schedule_list : list) -> str:
  res = "\t\t\t"
  for class_tuple in class_list:
    res += f"\t{class_tuple[0]}"

  for i in range(len(trainer_list)):
    res += f"\n{trainer_list[i][1]}"
    for has_class in schedule_list[i]:
      if has_class:
        res += "\tY"
      else:
        res += "\tN"
  return res

def get_trainers_with_num_classes(trainer_list : list, schedule_list : list) -> str:
  num_classes = input("Enter count of classes ---> ")
  if not num_classes.isdigit():
    return "Data Not Available"
  num_classes = int(num_classes)
  res = ""
  for i in range(len(schedule_list)):
    if sum(schedule_list[i]) == num_classes:
      res += f"{trainer_list[i][1]},"
  if not res:
    return "Data Not Available"
  return res[:-1]

if __name__ == "__main__":
  class_list = get_classes("gym_classes.txt")
  trainer_list = get_trainers("gym_trainer.txt")
  schedule_list = get_schedule("gym_schedule.txt")

  print("""___________________________________________________________ Menu
___________________________________________________________
1. Display the gym class name by its class ID
2. Display class ID by class name
3. Display which gym trainer(s) is running a class with a given class ID
4. Display gym classes that a trainer is running
5. Display trainer(s) name who is running a given number of classes
6. Display the trainer's salary
7. Display all classes with IDs
8. Display the details of all gym schedules and gym trainers
9. Exit the program""")

  # Asking loop
  while True:
    user_choice = input("enter your choice ---> ")

    if user_choice == "1":
      print(get_name_from_id(class_list))

    elif user_choice == "2":
      print(get_id_from_name(class_list))

    elif user_choice == "3":
      '''enter your choice ---> 3
Enter Class ID ---> 5
Amelia Grosh,Daniel Adams,Olive Greene'''
    elif user_choice == "4":
      pass
    elif user_choice == "5":
      pass
    elif user_choice == "6":
      print(get_wages(trainer_list, schedule_list))
    elif user_choice == "7":
      print(get_full_classes(class_list))
    elif user_choice == "8":
      print(get_full_schedule(class_list, trainer_list, schedule_list))
    elif user_choice == "9":
      print("Thank you for using our program. Bye!")
      exit(0)
    # If none of the options were gotten
    else:
      print("Wrong choice, please enter again")
