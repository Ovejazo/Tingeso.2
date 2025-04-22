const saveEmployee = (e) => {
  e.preventDefault();

  const employee = { rut, name, salary, children, category, id };

  // Mostrar en consola los tipos de datos que se están enviando
  console.log("Datos enviados:");
  console.log("rut:", rut, "tipo:", typeof rut);
  console.log("name:", name, "tipo:", typeof name);
  console.log("salary:", salary, "tipo:", typeof salary);
  console.log("children:", children, "tipo:", typeof children);
  console.log("category:", category, "tipo:", typeof category);
  console.log("id:", id, "tipo:", typeof id);
  console.log("Objeto completo:", employee);

  if (id) {
    // Actualizar Datos Empleado
    employeeService
      .update(employee)
      .then((response) => {
        console.log("Empleado ha sido actualizado.", response.data);
        navigate("/employee/list");
      })
      .catch((error) => {
        console.log(
          "Ha ocurrido un error al intentar actualizar datos del empleado.",
          error
        );
      });
  } else {
    // Crear nuevo empleado
    employeeService
      .create(employee)
      .then((response) => {
        console.log("Empleado ha sido añadido.", response.data);
        navigate("/employee/list");
      })
      .catch((error) => {
        console.log(
          "Ha ocurrido un error al intentar crear nuevo empleado.",
          error
        );
      });
  }
};