from injector import singleton
from costum_agent import Model


def configure(binder):
    binder.bind(Model, to=Model, scope=singleton)
