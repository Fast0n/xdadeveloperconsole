import sqlite3
import constants

class PyQL:
    def __init__(self, db_name):
        self.db_name = db_name

    def __in(self, operator, key, value, logic_op):
        if operator == "IN" or operator == "NOT IN":
            if isinstance(value[1], list):
                values = value[1]
                value[1] = ()
                for i in values:
                    value[1] += i

            value = str(value[1])

            if value[-2] == ",":
                value = f"{value[:-2]})"

            condition = f"{key} {operator} {value} {logic_op} "
        else:
            condition = None

        return condition

    def __between(self, operator, key, value, logic_op):
        if operator == "BETWEEN" or operator == "NOT BETWEEN":
            condition = f"{key} {operator} {value[0]} AND {value[1]} {logic_op} "
        else:
            condition = None
        return condition

    def __get_logic_operator(self, value, pos):
        if isinstance(value, list) and value[pos] in constants.LOGIC_OPERATORS:
            logic_op = value[pos].upper()
        else:
            logic_op = "AND"
        return logic_op

    def __get_operator(self, value):
        if isinstance(value, list) and not isinstance(value[0], list) and value[0] in constants.OPERATORS:
            operator = value[0].upper()
        else:
            operator = "="

        return operator

    def __check(self, value):
        return isinstance(value, list) and isinstance(value[0], list)

    def __get_value(self, value):
        if not isinstance(value, list):
            return value
        elif value[-1] in constants.LOGIC_OPERATORS:
            return value[-2]
        elif value[0] in constants.OPERATORS:
            return value[1]

    def __check_raw(self, key, value):
        return key == "raw" and isinstance(value, tuple)

    def __condition(self, conditions):
        if conditions:
            condition = " WHERE "
            for key, value in conditions.items():

                if self.__check_raw(key, value):
                    return f" {value[0]}"

                def repeat(values):
                    operator = self.__get_operator(values)
                    logic_op = self.__get_logic_operator(values, 1)
                    value = self.__get_value(values)

                    between = self.__between(operator, key, values, logic_op)
                    in_ = self.__in(operator, key, values, logic_op)

                    if between:
                        condition = between
                    elif in_:
                        condition = in_
                    else:
                        condition = f"{key} {operator} '{value}' {logic_op} "

                    return condition

                if self.__check(value):
                    for i in value:
                        condition += repeat(i)
                else:
                    condition += repeat(value)

            condition = condition[0:-5]

        else:
            condition = ""

        return condition

    def create_table(self, table, *args, **kwargs):
        conn = sqlite3.connect(self.db_name)
        cursor = conn.cursor()

        campi = ""

        if kwargs.items():
            for key, value in kwargs.items():
                campi += f"{key} "
                for k, v in value.items():
                    if isinstance(v, bool) and v:
                        campi += f"{k} "
                    else:
                        campi += f"{k}({v}) "
                else:
                    campi = f"{campi[:-1]}, "
        else:
            campi = f"{campi[:-2]}"

        if args:
            for i, value in enumerate(args):
                print(args)
                if i == 0: campi += ", "
                for k, v in value.items():
                    if isinstance(v, bool) and v:
                        campi += f"{k} "
                    else:
                        campi += f"{k}({v}) "
                else:
                    campi = f"{campi[:-1]}, "
        else:
            campi = f"{campi[:-2]}"

        cursor.execute(f"CREATE TABLE {table} ({campi});")
        conn.commit()
        conn.close()

    def insert(self, table, columns, *values):
        columns = str(columns).replace("'", "")
        mark = ""
        t = ()
        for value in values:
            t += value
            mark += "("
            for i in value:
                mark += "?, "
            mark = f"{mark[:-2]}), "
        mark = mark[:-2]
        conn = sqlite3.connect(self.db_name)
        cursor = conn.cursor()

        cursor.execute(f"INSERT INTO {table} {columns} VALUES {mark}", t)
        conn.commit()
        conn.close()
        return

    def select(self, select="*", *tables, **conditions):

        select = str(select).replace("'", "").replace("(", "").replace(")", "")
        conn = sqlite3.connect(self.db_name)
        cursor = conn.cursor()

        query_table = ""
        for table in tables:
            query_table += f"{table}, "
        else:
            query_table = query_table[:-2]


        # if distinct: distinct = " DISTINCT"
        # else: distinct = ""

        condition = self.__condition(conditions)

        result = cursor.execute(f"SELECT {select} FROM {query_table}{condition}").fetchall()
        conn.close()
        return result

    def update(self, table, columns, set, **conditions):
        conn = sqlite3.connect(self.db_name)
        cursor = conn.cursor()
        update = ""
        for key in columns:
            update += f"{key} = ?, "
        update = update[:-2]

        condition = self.__condition(conditions)

        cursor.execute(f"UPDATE {table} SET {update}{condition}", set)
        conn.commit()
        conn.close()
        return

    def delete(self, table, **conditions):
        conn = sqlite3.connect(self.db_name)
        cursor = conn.cursor()

        condition = self.__condition(conditions)

        cursor.execute(f"DELETE FROM {table}{condition}")
        conn.commit()
        conn.close()
        return
