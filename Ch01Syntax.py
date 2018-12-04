# -*- coding: UTF-8 -*-

'''
==== https://docs.python.org/3/tutorial/index.html ====

==== 1. Using the Python Interpreter ====
The Python interpreter is usually installed as /usr/local/bin/python${version}

  == 2.1. Invoking the Interpreter ==
  windows
    -> set path=%path%;${python_path}\bin

==== 2. The Interpreter and Its Environment ====
By default, Python source files are treated as encoded in UTF-8.
_Python 3.x 中，python 直譯器預期的 .py 編碼，預設是 UTF-8

  == 2.1. Source Code Encoding ==
  To declare an encoding other than the default one, a special comment line should be added as the first line of the file.
  _要宣告預設的 UTF-8 以外的編碼，在第一行加入如下的註解
  
      # -*- coding: ${encoding} -*-
      
      ex: # -*- coding: cp1252 -*-
 
  One exception to the first line rule is when the source code starts with a UNIX "shebang" line.
  In this case, the encoding declaration should be added as the second line of the file.

      #!/usr/bin/env python3
      # -*- coding: cp1252 -*-
'''
msg = '今天天氣真好'
print(msg);
print('字數: ' , len(msg)); # 6

msg = u'今天天氣真好'
print(msg);
print('字數: ' , len(msg)); # 6

'''
==== 3. Assigning Values to Variables ====
Python variables do not need explicit declaration to reserve memory space.
The declaration happens automatically when you assign a value to a variable.
The equal sign (=) is used to assign values to variables.

  == 3.1. Standard Data Types ==
  Python has five standard data types
    1. Numbers
    2. String
    3. List
    4. Tuple
    5. Dictionary
    
  == 3.2. Numbers ==
  Number data types store numeric values.
  Number objects are created when you assign a value to them.
  Python supports four different numerical types
    1. int     (signed integers)
    2. float   (floating point real values)
    3. complex (complex numbers)
'''
print("\n==== Numbers ====");
val = 10;
print(type(val)); # <class 'int'>
val = 10.5;
print(type(val)); # <class 'float'>
val = True;
print(type(val)); # <class 'bool'>
val = 10 + 5j;
print(type(val)); # <class 'complex'>

print(10);   # 10 (10進位)
print(0b10); # 2  (2進位)
print(0o10); # 8  (8進位)
print(0x10); # 16 (16進位)

'''
  == 3.3. Strings ==
  String can be enclosed in single quotes ('...') or double quotes ("...")
  _字串可以單引號或雙引號括住
  
  \ can be used to escape quotes
  _ \ 可以 escape '(單引號) 和 "(雙引號)
   
  Subsets of strings can be taken using the slice operator ([ ] and [:] )
    with indexes starting at 0 in the beginning of the string and working their way from -1 at the end.
  _可以使用 [] 和 [:] 切割字串
  
  The plus (+) sign is the string concatenation operator and the asterisk (*) is the repetition operator
  
  If you don’t want characters prefaced by \ to be interpreted as special characters,
    you can use raw strings by adding an r before the first quote  
'''
print("\n==== Strings ====");
val = "Hello World!"
print(val);          # Hello World!
print(val[0]);       # H          (Prints first character of the string)
print(val[2:5]);     # llo        (Prints characters starting from 3rd to 5th)
print(val[2:]);      # llo World! (Prints string starting from 3rd character)
print(val * 2);      # Hello World!Hello World!
print(val + "TEST"); # Hello World!TEST
print("C:\some\name");  #              (\n 會被解讀為換行字元)
print(r"C:\some\name"); # C:\some\name (在第一個 " 之前加上 r，會變成 raw strings)

'''
  == 3.4. Lists ==
'''
print("\n==== Lists ====");
val = ['abcd', 786, 2.23, 'john', 70.2];
print(val);      # ['abcd', 786, 2.23, 'john', 70.2]
print(val[0]);   # abcd
print(val[1:3]); # [786, 2.23]          (Prints elements starting from 2nd till 3rd) 
print(val[2:]);  # [2.23, 'john', 70.2] (Prints elements starting from 3rd element)
print(val * 2);  # ['abcd', 786, 2.23, 'john', 70.2, 'abcd', 786, 2.23, 'john', 70.2]
